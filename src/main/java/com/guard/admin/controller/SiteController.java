package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.payload.response.PhotoResponse;
import com.guard.admin.payload.response.ReportResponse;
import com.guard.admin.payload.response.VisitorResponse;
import com.google.firebase.messaging.FirebaseMessaging;
import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Photo;
import com.guard.admin.database.entities.Visitor;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.database.repositories.PhotoRepository;
import com.guard.admin.database.repositories.SiteRepository;
import com.guard.admin.database.repositories.VisitorRepository;
import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.payload.request.FileRequest;
import com.guard.admin.payload.request.ReportRequest;
import com.guard.admin.payload.request.VisitorRequest;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.NotificationService;
import com.guard.admin.service.declaration.PhotoService;
import com.guard.admin.service.declaration.ReportService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.guard.admin.service.declaration.SiteService;
import com.guard.admin.service.declaration.VisitorService;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.ArrayList;

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

    @Autowired
    ReportService reportService;

    @Autowired
    VisitorService visitorService;

    @Autowired
    PhotoService photoService;

    @Autowired
    NotificationService notificationService;

    private final String uploadDir = System.getProperty("user.dir") + File.separator + "upload/public/images";
    
    @Operation(summary = "Create Site",
            description = "Create a new Site without HitPoints (Admin and Branch Manager) ", tags = { "Site Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> addSite(@RequestBody SiteWithHitpoint site) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                return ResponseEntity.ok(new ApiResponse<>(siteService.create(site)));
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
            description = "Image Upload for site", tags = {"Site Management"})
    @PostMapping(value = "/image", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> addImages(
        @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
        @ModelAttribute FileRequest fileRequest) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.guard)) {
                
                List<Photo> photoList = new ArrayList<>();
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
                    photoList.add(photo);
                }
                return ResponseEntity.ok(new ApiResponse<>("", photoList.get(0).getUrl()));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Image View",
            description = "Can view Images For site", tags = {"Site Management"})
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
            description = "Add Visitor for site", tags = {"Site Management"})
    @PostMapping(value = "/visitor", consumes = { "multipart/form-data" })
    public ResponseEntity<ApiResponse<?>> addVisitor(
        @Parameter(description = "Files to be uploaded", required = true, content = @Content(mediaType = "application/octet-stream"))
        @ModelAttribute VisitorRequest visitorRequest) {
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

                if(visitorRequest.getImage() != null) {
                    String fileName = System.currentTimeMillis() + "_" + visitorRequest.getImage().getOriginalFilename();
                    File destination = new File(uploadDir + File.separator + fileName);
                    visitorRequest.getImage().transferTo(destination);
                    visitor.setUrl(fileName);
                }
                
                visitor.setTimestamp(new Date());
                visitorRepository.save(visitor);
                return ResponseEntity.ok(new ApiResponse<>("ok"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get visitors for site",
            description = "Get Visitors for site", tags = {"Site Management"})
    @GetMapping("/visitors/{siteId}")
    public ResponseEntity<ApiResponse<List<VisitorResponse>>> getVisitors(
        @PathVariable Integer siteId
        , @RequestParam Integer pageNum
        , @RequestParam Integer pageSize
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate 
    ) {
        return ResponseEntity
                .ok(new ApiResponse<>(visitorService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));

        // try{
        //     // UserDetailsImpl userDetails = authService.getInfo();
        //     List<Visitor> visitorList = visitorRepository.findBySiteId(siteId);   
        //     for(Visitor visitor : visitorList){
        //         visitor.setSite(null);
        //     }         
        //     return new ResponseEntity<>(visitorList, HttpStatus.OK);
        // } catch(Exception e) {
        //     System.out.println(e);
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        // }
    }
    
     @Operation(summary = "Get photos for site",
            description = "Get Photos for site", tags = {"Site Management"})
    @GetMapping("/photos/{siteId}")
    public ResponseEntity<ApiResponse<List<PhotoResponse>>> getPhotos(
        @PathVariable Integer siteId
        , @RequestParam Integer pageNum
        , @RequestParam Integer pageSize
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate 
    ) {
        return ResponseEntity
                .ok(new ApiResponse<>(photoService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));
        
        // try{
        //     // UserDetailsImpl userDetails = authService.getInfo();
        //     List<Visitor> visitorList = visitorRepository.findBySiteId(siteId);   
        //     for(Visitor visitor : visitorList){
        //         visitor.setSite(null);
        //     }         
        //     return new ResponseEntity<>(visitorList, HttpStatus.OK);
        // } catch(Exception e) {
        //     System.out.println(e);
        //     return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        // }
    }


    @Operation(summary = "Create Report",
        description = "Send report for site (Guard)", tags = { "Site Management" })
    @PostMapping("/reports")
    public ResponseEntity<ApiResponse<?>> addReport(@RequestBody ReportRequest reportRequest) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.guard)) {
                return ResponseEntity.ok(new ApiResponse<>(reportService.create(reportRequest, userDetails.getId())));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
    @Operation(summary = "Approve Report",
        description = "Approve or Cancel report for site by Admin or Branch Manager or Area Manager", tags = { "Site Management" })
    @PutMapping("/reports/{reportId}")
    public ResponseEntity<ApiResponse<?>> approveReport(@PathVariable Integer reportId, @RequestBody Boolean isApproved) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.area) || userDetails.getRole().equals(Role.branch)) {
                boolean result = reportService.approveReport(reportId, userDetails.getId(), isApproved);
                if(result)
                    return ResponseEntity.ok(new ApiResponse<>(isApproved?"Successfully approved." : "Sucessfully canceled."));
                else
                    return ResponseEntity.badRequest().body(new ApiResponse<>("Error occured.",  userDetails.getId()));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Operation(summary = "Get Report History",
        description = "Get Report History For the specific site ( Guard cannot get it. ) ", tags = { "Site Management" })
    @GetMapping("/reports/{siteId}")
    public ResponseEntity<ApiResponse<List<ReportResponse>>> getReports(
        @PathVariable Integer siteId
        , @RequestParam Integer pageNum
        , @RequestParam Integer pageSize
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate
        , @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate 
    ) {
        UserDetailsImpl userDetails = authService.getInfo();
        if (userDetails.getRole().equals(Role.admin)) {
            return ResponseEntity
                    .ok(new ApiResponse<>(reportService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));
        } else if (userDetails.getRole().equals(Role.client)) {
            Integer clientId = userDetails.getId();
            if (clientId == siteRepository.findById(siteId).get().getClient().getId()) {
                return ResponseEntity.ok(
                        new ApiResponse<>(reportService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));
            }
        }
        // if(!userDetails.getRole().equals(Role.guard)) {
        // if(siteId != null)
        // return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(siteId, null, pageNum, pageSize, startDate, endDate)));
        //     else if(userDetails.getRole().equals(Role.admin))
        //         return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(null, null, pageNum, pageSize, startDate, endDate)));
        //     else
        //         return ResponseEntity.ok(new ApiResponse<>(reportService.getPage(null, userDetails.getId(),pageNum, pageSize, startDate, endDate)));
        // }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action!"));
    }
    
    @Operation(summary = "Test Firebase Messaging",
        description = "Test Firebase Messaging ", tags = { "Site Management" })
    @GetMapping("/test")
    public void testMessage() {
        String token = "cs7k-JuzTB-tMInsHrGzGX:APA91bGjH1PK5l-ptiPUELPz6s2d7x28hU65Bcq6v0kzrezs4zfuPnik7I_g4cVncbxSzm2X7lGiMrytcr3dZkxw-P9VKj8SvrLO7RumQoi02nen7K2CB7QGFVmJ8-K-K6oCo-mzBYvj";

        notificationService.sendMessage(token, "Title", "Description");
    }
}
