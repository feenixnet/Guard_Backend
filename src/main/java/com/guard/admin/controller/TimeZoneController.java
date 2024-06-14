package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.payload.response.MessageResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.TimeZoneService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/timezone")
public class TimeZoneController {
    @Autowired
    TimeZoneService timeZoneService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Update TimeZone",
            description = "Update the TimeZone for all site (Only Admin)", tags = { "TimeZone Management" })
    @PutMapping("/")
    public ResponseEntity<ApiResponse<?>> save(@RequestParam String timezone) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                return ResponseEntity.ok(new ApiResponse<>(new MessageResponse(timeZoneService.put(timezone))));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get TimeZone",
            description = "Get the TimeZone for all site ", tags = { "TimeZone Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get() {
        try{
            return ResponseEntity.ok(new ApiResponse<>(new MessageResponse(timeZoneService.get())));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
}
