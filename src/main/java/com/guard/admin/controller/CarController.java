package com.guard.admin.controller;

import com.guard.admin.database.entities.Car;
import com.guard.admin.service.declaration.CarService;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;

import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;


import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/cars")
public class CarController {

    @Autowired
    AuthService authService;

    @Autowired
    CarService carService;

    @Operation(summary = "Create Car",
            description = "Create a new Car(Admin and Branch Manager) ", tags = { "Car Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> add(@RequestBody Car car) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                return ResponseEntity.ok(new ApiResponse<>(carService.create(car)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));
        }
    }

    @Operation(summary = "Delete Car",
            description = "Delete the Car from the Car List (Admin, Branch manager)", tags = { "Car Management" })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> delete(@PathVariable Integer id) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                carService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Update Car",
            description = "Update the Car (Only Admin)", tags = { "Car Management" })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> update(@PathVariable Integer id ,@RequestBody Car car) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch)) {
                carService.update(id, car);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get all car list.",
            description = "Get car list on the web application", tags = { "Car Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get(
              @RequestParam(required = false) Integer pageNum
            , @RequestParam(required = false) Integer pageLength
            , @RequestParam(required = false) String search
    ) {
        UserDetailsImpl userDetails = authService.getInfo();

        if(pageNum != null) {
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch))
                return ResponseEntity.ok(new ApiResponse<>(carService.getPage(pageNum, pageLength, search)));
        } else {
            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch))
                return ResponseEntity.ok(new ApiResponse<>(carService.getAll()));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }

    @Operation(summary = "Get Car by id",
            description = "Get Car on the web application", tags = { "Client Management" })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getClient(@PathVariable Integer id) {
        UserDetailsImpl userDetails = authService.getInfo();
        if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch))
            return ResponseEntity.ok(new ApiResponse<>(carService.get(id)));
        return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
    }


}
