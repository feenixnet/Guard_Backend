package com.guard.admin.controller;

import com.guard.admin.payload.request.ReportRequest;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.ReportService;
import com.guard.admin.service.declaration.ScheduleService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/reports")
public class ReportController {

    @Autowired
    ReportService reportService;

    @Autowired
    AuthService authService;
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Operation(summary = "Create Report",
            description = "Send report to the server (Guard)", tags = { "Report Management" })
    @PostMapping(value = "/", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> addReport(
            @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
            @ModelAttribute ReportRequest reportRequest
    ) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            logger.info(userDetails.getRole());
            if(userDetails.getRole().equals(Role.guard)) {
                return ResponseEntity.ok(new ApiResponse<>(reportService.create(reportRequest, userDetails.getId())));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get Report History",
            description = "Get Report History from the Server ( Not Guard) ", tags = { "Report Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> getGuardItem(
            @RequestParam(required = false) Integer siteId
            , @RequestParam Integer pageNum
            , @RequestParam Integer pageSize
            , @RequestParam(required = false) Date startDate
            , @RequestParam(required = false) Date endDate
            ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if(!userDetails.getRole().equals(Role.guard)) {
            if(siteId != null)
                return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));
            else if(userDetails.getRole().equals(Role.admin))
                return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(null, null, pageNum, pageSize, startDate, endDate)));
            else
                return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(null, userDetails.getId(),pageNum, pageSize, startDate, endDate)));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
    }
}
