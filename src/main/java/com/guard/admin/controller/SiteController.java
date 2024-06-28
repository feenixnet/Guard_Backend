package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Photo;
import com.guard.admin.database.entities.Visitor;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.PhotoRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.database.repositories.VisitorRepository;
import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.payload.request.FileRequest;
import com.guard.admin.payload.request.VisitorRequest;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.method.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.guard.admin.service.declaration.SiteService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/sites")
public class SiteController {

    @Autowired
    SiteService siteService;

    @Autowired
    AuthService authService;


    @Autowired
    SiteRepository siteRepository;

    @Autowired
    GuardRepository guardRepository;

    @Autowired
    PhotoRepository photoRepository;

    @Autowired
    VisitorRepository visitorRepository;


    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";

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

    @Operation(summary = "Image Upload for site",
            description = "Image Upload for site", tags = {"Image Upload"})
    @PostMapping(value = "/image", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> addImages(
        @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
        @ModelAttribute FileRequest fileRequest) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.guard)) {
                

                for(MultipartFile file: fileRequest.getImage())
                {
                    String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
                    File destination = new File(uploadDir + File.separator + fileName);
                    file.transferTo(destination);
                    Photo photo = new Photo();
                    photo.setSite(siteRepository.findById(fileRequest.getSiteId()).get());
                    photo.setGuard(guardRepository.findById(userDetails.getId()).get());
                    photo.setTimestamp(new Date());
                    photo.setUrl(fileName);
                    photoRepository.save(photo);
                }
                return ResponseEntity.ok(new ApiResponse<>("ok"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Image View",
            description = "Can view Images For site", tags = {"Image View"})
    @GetMapping("/images/{siteId}")
    public ResponseEntity<List<Photo>> getImages(@PathVariable Integer siteId) {
        try{
            // UserDetailsImpl userDetails = authService.getInfo();
            List<Photo> photoList = photoRepository.findBySiteId(siteId);   
            for(Photo photo : photoList){
                photo.setSite(null);
            }         
            return new ResponseEntity<>(photoList, HttpStatus.OK);
        } catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Add Visitor for site",
            description = "Add Visitor for site", tags = {"Add Visitor"})
    @PostMapping("/visitor")
    public ResponseEntity<ApiResponse<?>> addVisitor(@RequestBody VisitorRequest visitorRequest) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.guard)) {
                Visitor visitor = new Visitor();
                visitor.setSite(siteRepository.findById(visitorRequest.getSite_id()).get());
                visitor.setCompany(visitorRequest.getCompany());
                visitor.setFullname(visitorRequest.getFullname());
                visitor.setPhonenumber(visitorRequest.getPhonenumber());
                visitor.setLicenseplate(visitorRequest.getLicenseplate());
                visitor.setReason(visitorRequest.getReason());
                visitor.setGuard(guardRepository.findById(userDetails.getId()).get());
                visitor.setUrl(visitorRequest.getUrl());
                visitor.setTimestamp(new Date());
                visitorRepository.save(visitor);
                return ResponseEntity.ok(new ApiResponse<>("ok"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @GetMapping("/visitors/{siteId}")
    public ResponseEntity<List<Visitor>> getVisitors(@PathVariable Integer siteId) {
        try{
            // UserDetailsImpl userDetails = authService.getInfo();
            List<Visitor> visitorList = visitorRepository.findBySiteId(siteId);   
            for(Visitor visitor : visitorList){
                visitor.setSite(null);
            }         
            return new ResponseEntity<>(visitorList, HttpStatus.OK);
        } catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }


}
