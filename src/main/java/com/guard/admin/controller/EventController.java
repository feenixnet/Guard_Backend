package com.guard.admin.controller;

import com.guard.admin.database.entities.Event;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.EventService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/events")
public class EventController {

    @Autowired
    EventService eventService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Add Event",
            description = "Save the Event on the Server", tags = { "Event Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> create(@RequestBody Event event) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.guard)) {
                return ResponseEntity.ok(new ApiResponse<>(eventService.create(event)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get All Guards",
            description = "Update the Schedule for the Site using Site Id", tags = { "Event Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get(
            @RequestParam(required = false) Integer siteId,
            @RequestParam Integer pageNum,
            @RequestParam Integer pageSize,
            @RequestParam(required = false) Date startDate,
            @RequestParam(required = false) Date endDate
    ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if(!userDetails.getRole().equals(Role.guard)) {
            if(siteId != null)
                return ResponseEntity.ok(new ApiResponse<>(eventService.getPage(siteId,null, null, pageNum, pageSize, startDate, endDate)));
            else if(userDetails.getRole().equals(Role.admin))
                return ResponseEntity.ok(new ApiResponse<>(eventService.getPage(null, null, null, pageNum, pageSize, startDate, endDate)));
            else return ResponseEntity.ok(new ApiResponse<>(eventService.getPage(null, userDetails.getId(), null, pageNum, pageSize, startDate, endDate)));
        } else {
            return ResponseEntity.ok(new ApiResponse<>(eventService.getPage(null, null , userDetails.getId(), pageNum, pageSize, startDate, endDate)));
        }
    }
}