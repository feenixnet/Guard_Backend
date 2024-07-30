package com.guard.admin.controller;

import com.guard.admin.payload.dto.SiteWithHitpoint;
import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.payload.response.ClientDashboardResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.service.declaration.ClientService;
import com.guard.admin.service.declaration.SiteService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Event;
import com.guard.admin.database.entities.Site;
import com.guard.admin.database.repositories.EventRepository;
import com.guard.admin.database.repositories.SiteRepository;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/clients")
public class ClientController {

    @Autowired
    ClientService clientService;

    @Autowired
    AuthService authService;

    @Autowired
    SiteService siteService;

    @Autowired
    SiteRepository siteRepository;

    @Autowired
    EventRepository eventRepository;

    @Operation(summary = "Add Clients",
            description = "Create the Clients at the Admin Website (Only Admin)", tags = { "Client Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> addClient(@RequestBody Client client) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin))
            {
                if (authService.isDuplicate((client.getEmail()))) {
                    return ResponseEntity.ok(new ApiResponse<>(clientService.create(client)));
                }
                return ResponseEntity.badRequest().body(new ApiResponse<>("Same email address already registered!"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Operation(summary = "Delete Clients",
            description = "Delete the Client from the Admin Webpage (Admin only)", tags = { "Client Management" })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> removeClient(@PathVariable Integer id) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                clientService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Update Client",
            description = "Update the Client by client Id (Only Admin and Client)", tags = { "Client Management" })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> updateClient(@PathVariable Integer id ,@RequestBody Client clientDetail) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            System.err.println("XXXXXXXX");
            if (userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client)) {
                
                clientService.update(id, clientDetail);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get Clients",
            description = "Get client list on the web application", tags = { "Client Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get(
              @RequestParam(required = false) Integer pageNum
            , @RequestParam(required = false) Integer pageLength
            , @RequestParam(required = false) String search
    ) {
        UserDetailsImpl userDetails = authService.getInfo();

        if(pageNum != null) {
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client))
                return ResponseEntity.ok(new ApiResponse<>(clientService.getPage(pageNum, pageLength, search)));
        } else {
            if(userDetails.getRole().equals((Role.admin)) || userDetails.getRole().equals(Role.client))
                return ResponseEntity.ok(new ApiResponse<>(clientService.getAll()));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }

    @Operation(summary = "Get Client",
            description = "Get Client on the web application", tags = { "Client Management" })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getClient(@PathVariable Integer id) {
        UserDetailsImpl userDetails = authService.getInfo();
        if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.client))
            return ResponseEntity.ok(new ApiResponse<>(clientService.get(id)));
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }

    @Operation(summary = "Get Site and Event",
            description = "Get Site list and Latest Events for Client", tags = { "Client Management" })
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<?>> getDashboard() {
        UserDetailsImpl userDetails = authService.getInfo();
        if(userDetails.getRole().equals(Role.client))
        {
            Integer clientId = userDetails.getId();
            List<Site> siteList = siteRepository.findAllByClientId(clientId);
            List<SiteWithHitpoint> siteWithHitpointList = new ArrayList<>();  

            List<Integer> siteIdList = new ArrayList<>();
            for(Site site : siteList)
            {
                siteIdList.add(site.getId());
                siteWithHitpointList.add(siteService.getFull(site.getId()));
            }

            ClientDashboardResponse clientDashboardResponse = new ClientDashboardResponse();
            clientDashboardResponse.setSiteList(siteWithHitpointList);
            clientDashboardResponse.setEventList(eventRepository.findTop100BySiteIdInOrderByTimestampDesc(siteIdList));
            
            return ResponseEntity.ok(new ApiResponse<>(clientDashboardResponse));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }
}
