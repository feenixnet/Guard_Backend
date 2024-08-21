package com.guard.admin.controller;

import com.guard.admin.database.entities.Setting;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.payload.response.MessageResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.BugService;
import com.guard.admin.service.declaration.SettingService;
import com.guard.admin.utils.constant.Role;
import com.guard.admin.payload.request.SettingRequest;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/setting")
public class SettingController {
    @Autowired
    SettingService settingService;

    @Autowired
    BugService bugService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Update Setting",
            description = "Update the Setting for all site (Only Admin)", tags = { "Setting Management" })
    @PutMapping("/")
    public ResponseEntity<ApiResponse<?>> updateSetting(
        @Parameter(description = "Files to be uploaded", required = false, content = @Content(mediaType = "application/octet-stream"))    
        @ModelAttribute SettingRequest setting) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                return ResponseEntity.ok(new ApiResponse<>(settingService.put(setting)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get Setting",
            description = "Get the Setting for all site ", tags = { "Setting Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getSetting() {
        try{
            return ResponseEntity.ok(new ApiResponse<>(settingService.get()));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Save reported Bug",
            description = "Save reported Bug", tags = { "Setting Management" })
    @PutMapping("/bugs")
    public ResponseEntity<ApiResponse<?>> ReportBug(   
        @ModelAttribute String bugText) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            return ResponseEntity.ok(new ApiResponse<>(bugService.create(userDetails, bugText)));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
}
