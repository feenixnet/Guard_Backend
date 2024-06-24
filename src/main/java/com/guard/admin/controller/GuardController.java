package com.guard.admin.controller;

import com.guard.admin.payload.request.GuardRequest;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.service.declaration.GuardService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/guards")
public class GuardController {

    @Autowired
    GuardService guardService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Add Guard",
            description = "Create a new Guard (Only Admin)", tags = {"Guard Management"})
    @PostMapping(value = "/" , consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> Add(
            @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
            @ModelAttribute GuardRequest guard
    ) {
        try {
            UserDetailsImpl userDetails = authService.getInfo();
            if (userDetails.getRole().equals(Role.admin)) {
                if (authService.isDuplicate((guard.getEmail()))) {
                    return ResponseEntity.ok(new ApiResponse<>(guardService.create(guard)));
                } else
                    return ResponseEntity.badRequest().body(new ApiResponse<>("Same email address already registered!"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to add Guard"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Operation(summary = "Delete Guard",
            description = "Delete the Guard from the Server using Site Id (Only Admin)", tags = {"Guard Management"})
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> remove(@PathVariable Integer id) {
        try {
            UserDetailsImpl userDetails = authService.getInfo();
            
            if (userDetails.getRole().equals(Role.admin)) {
                guardService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Operation(summary = "Update Guard",
            description = "Update the Schedule for the Site using Site Id (Only Guard and Admin)", tags = {"Guard Management"})
    @PutMapping(value = "{id}" , consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> updateGuard(@PathVariable Integer id,
          @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
          @ModelAttribute GuardRequest guardDetail
    ) {
        try {
            UserDetailsImpl userDetails = authService.getInfo();
            if (userDetails.getRole().equals(Role.guard) || userDetails.getRole().equals(Role.admin)) {
                return ResponseEntity.ok(new ApiResponse<>(guardService.update(id, guardDetail)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
        } catch (Exception ignored) {
            return ResponseEntity.badRequest().body(new ApiResponse<>("error"));
        }
    }

    @Operation(summary = "Get Guards",
            description = "Get Guard List for Users (Admin, Branch, Area , Client)", tags = {"Guard Management"})
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getAll(
            @RequestParam(required = false) Integer siteId
            , @RequestParam(required = false) String type
            , @RequestParam(required = false) Integer pageNum
            , @RequestParam(required = false) Integer pageLength
            , @RequestParam(required = false) String search
    ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if (pageNum != null) {
            if (userDetails.getRole().equals(Role.admin))
                return ResponseEntity.ok(new ApiResponse<>(guardService.getPage(pageNum, pageLength, search)));
        } else if (siteId != null) {
            if (userDetails.getRole().equals(Role.client))
                return ResponseEntity.ok(new ApiResponse<>(guardService.getBySiteId(siteId)));
        } else if (type != null) {
            if (userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.area) || userDetails.getRole().equals(Role.dispatch))
                return ResponseEntity.ok(new ApiResponse<>(guardService.getByType(type)));
        } else {
            if (userDetails.getRole().equals(Role.admin))
                return ResponseEntity.ok(new ApiResponse<>(guardService.getAll()));
            else if (userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area))
                return ResponseEntity.ok(new ApiResponse<>(guardService.getByUserId(userDetails.getId())));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }

    @Operation(summary = "Get Guard",
            description = "Get Guard for Users (Admin, Branch, Area , Client)", tags = {"Guard Management"})
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getOne(
            @PathVariable Integer id
    ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if (userDetails.getRole().equals(Role.admin))
            return ResponseEntity.ok(new ApiResponse<>(guardService.get(id)));
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }
}
