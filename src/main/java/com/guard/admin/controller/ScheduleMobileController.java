package com.guard.admin.controller;

import com.guard.admin.database.entities.ScheduleMobile;
import com.guard.admin.payload.dto.Appointment;
import com.guard.admin.payload.dto.ScheduleMobileData;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.ScheduleMobileService;
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
@RequestMapping("api/schedule_mobile")
public class ScheduleMobileController {
    @Autowired
    ScheduleMobileService scheduleMobileService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Get Schedule-Mobile",
            description = "Get Schedule-Mobile for the Car by Car Id (Only Staff)", tags = { "Schedule(Mobile) Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get(
              @RequestParam(required = false) Integer carId
            , @RequestParam(required = false) String startTime
            , @RequestParam(required = false) String endTime
            ) {
        System.out.println("Hello");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

        UserDetailsImpl userDetails = authService.getInfo();

        if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client) || userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)) {
            System.out.println("OKDDDD");
            System.out.println(carId);
            List<Appointment> appointmentList = new ArrayList<>();
            List<ScheduleMobile> scheduleMobileList = new ArrayList<>();
            if(carId != null){
                scheduleMobileList = scheduleMobileService.planForManager(userDetails.getRole(), carId);
            }
                
            else {
                if(userDetails.getRole().equals(Role.admin))
                    scheduleMobileList = scheduleMobileService.findAll();
                return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
            }

            for(ScheduleMobile scheduleMobile : scheduleMobileList) {
                Appointment appointment = new Appointment();
                appointment.setText(scheduleMobile.getGuard().getFirstname() + scheduleMobile.getGuard().getLastname());
                appointment.setStartDate(scheduleMobile.getStartTime());
                appointment.setEndDate(scheduleMobile.getEndTime());
                // appointment.setDescription(schedule.getRule());
                appointment.setGuardId(scheduleMobile.getGuard().getId());
                appointment.setFrequency(scheduleMobile.getFrequency());
                appointment.setHours(scheduleMobile.getHours());
                appointment.setAnnounces(scheduleMobile.getAnnounces());
                appointmentList.add(appointment);
            }
            return ResponseEntity.ok(new ApiResponse<>(appointmentList));
        }
        else if(userDetails.getRole().equals(Role.guard) && startTime != null && endTime != null) {
            try {
                return ResponseEntity.ok(new ApiResponse<>(scheduleMobileService.planForGuard(userDetails.getId(), formatter.parse(startTime), formatter.parse(endTime))));
            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
                
            }
        }

        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
    }

    @Operation(summary = "Update Schedule(Mobile)",
            description = "Update the Schedule(Mobile) for the Car using Car Id (Only Staff)", tags = { "Schedule(Mobile) Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> update(@RequestBody ScheduleMobileData scheduleMobileData) {
        UserDetailsImpl userDetails = authService.getInfo();
        try {
            if (userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client) || userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)){
                return ResponseEntity.ok(new ApiResponse<>(scheduleMobileService.save(scheduleMobileData)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }
}
