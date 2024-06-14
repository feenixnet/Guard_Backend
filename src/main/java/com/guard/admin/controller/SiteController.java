package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.service.declaration.SiteService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/sites")
public class SiteController {

    @Autowired
    SiteService siteService;

    @Autowired
    AuthService authService;

    @Operation(summary = "Create Site",
            description = "Create a new Site without HitPoints (Admin and Branch Manager) ", tags = { "Site Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> addSite(@RequestBody SiteWithHitpoint Site) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.create(Site)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Delete Site",
            description = "Delete the Site from the Site List (Admin, Branch manager)", tags = { "Site Management" })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> removeSite(@PathVariable Integer id) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                siteService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Update Site",
            description = "Update the Site Position and HitPoint positions (Only Admin)", tags = { "Site Management" })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateSiteWithHitPoints(@PathVariable Integer id ,@RequestBody SiteWithHitpoint site) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.update(id, site)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get Site",
            description = "Get Site or Site List which assigned to them (Admin, Branch Manager, Area Manager, Client)", tags = { "Site Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getAllSites(
              @RequestParam(required = false) Integer pageNum
            , @RequestParam(required = false) Integer pageLength
            , @RequestParam(required = false) String search
    ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if(pageNum != null) {
            if(userDetails.getRole().equals(Role.admin))
            {
                return ResponseEntity.ok(new ApiResponse<>(siteService.getPage(pageNum, pageLength, search, 0)));
            }
            else if(userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.getPage(pageNum, pageLength, search, userDetails.getId())));
            }
        }
        else {
            if(userDetails.getRole().equals(Role.admin) ){
                return ResponseEntity.ok(new ApiResponse<>(siteService.getByAdmin()));
            }
            else if(userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.getByUser(userDetails.getId())));
            }
            else if(userDetails.getRole().equals(Role.client)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.getByClient(userDetails.getId())));
            }
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
    }

    @Operation(summary = "Get Site",
            description = "Get Site or Site List which assigned to them (Admin, Branch Manager, Area Manager, Client)", tags = { "Site Management" })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getSites(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(new ApiResponse<>(siteService.getFull(id)));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }
}
