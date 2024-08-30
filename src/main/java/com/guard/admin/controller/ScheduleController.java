package com.guard.admin.controller;

import com.guard.admin.database.entities.Schedule;
import com.guard.admin.payload.dto.Appointment;
import com.guard.admin.payload.dto.ScheduleData;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.ScheduleService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/schedule")
public class ScheduleController {
    @Autowired
    ScheduleService scheduleService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Get Schedule",
            description = "Get Schedule for the Site by Site Id (Only Staff)", tags = { "Schedule Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> admin(
              @RequestParam(required = false) Integer siteId
            , @RequestParam(required = false) String startTime
            , @RequestParam(required = false) String endTime
            ) {
        try{
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            UserDetailsImpl userDetails = authService.getInfo();

            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client) || userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)) {
                System.out.println("OKDDDD");
                System.out.println(siteId);
                List<Appointment> appointmentList = new ArrayList<>();
                List<Schedule> scheduleList = new ArrayList<>();
                if(siteId != null){
                    scheduleList = scheduleService.planForManager(userDetails.getRole(), siteId);
                }
                    
                else {
                    if(userDetails.getRole().equals(Role.admin))
                        scheduleList = scheduleService.findAll();
                    return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
                }

                for(Schedule schedule : scheduleList) {
                    Appointment appointment = new Appointment();
                    appointment.setText(schedule.getGuard().getFirstname() + schedule.getGuard().getLastname());
                    appointment.setStartDate(schedule.getStartTime());
                    appointment.setEndDate(schedule.getEndTime());
                    // appointment.setDescription(schedule.getRule());
                    appointment.setGuardId(schedule.getGuard().getId());
                    appointment.setFrequency(schedule.getFrequency());
                    appointment.setHours(schedule.getHours());
                    appointment.setAnnounces(schedule.getAnnounces());
                    appointmentList.add(appointment);
                }
                return ResponseEntity.ok(new ApiResponse<>(appointmentList));
            }
            else if(userDetails.getRole().equals(Role.guard) && startTime != null && endTime != null) {
                try {
                    return ResponseEntity.ok(new ApiResponse<>(scheduleService.planForGuard(userDetails.getId(), formatter.parse(startTime), formatter.parse(endTime))));
                } catch (ParseException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
                    
                }
            }

            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Update Schedule",
            description = "Update the Schedule for the Site using Site Id (Only Staff)", tags = { "Schedule Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> save(@RequestBody ScheduleData scheduleData) {
        UserDetailsImpl userDetails = authService.getInfo();
        try {
            if (userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client) || userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)){
                return ResponseEntity.ok(new ApiResponse<>(scheduleService.save(scheduleData)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }
}
