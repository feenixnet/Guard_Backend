package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.NotificationService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/device")
public class DeviceController {

    @Autowired
    NotificationService tokenService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Save Token",
            description = "Save Device Token To the Server", tags = { "Device Token Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> addClient(@RequestParam String token) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(!userDetails.getRole().equals(Role.guard)) {
                tokenService.saveToken(token, userDetails.getId(), userDetails.getRole());
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
}
