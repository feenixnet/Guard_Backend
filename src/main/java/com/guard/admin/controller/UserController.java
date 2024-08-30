package com.guard.admin.controller;

import com.guard.admin.payload.response.ApiResponse;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.service.declaration.AuthService;
import com.guard.admin.utils.constant.Role;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.database.entities.User;
import com.guard.admin.service.declaration.UserService;


@CrossOrigin(origins = "*")
@RestController
@RequestMapping("api/users")
public class UserController {

    @Autowired UserService userService;

    @Autowired AuthService authService;

    @Operation(summary = "Add new User", description = "Create user at the Admin Website", tags = { "User Management" })
    @PostMapping("/")
    public ResponseEntity<ApiResponse<?>> addUser(@RequestBody User user) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                if (authService.isDuplicate((user.getEmail()))) {
                    return ResponseEntity.ok(new ApiResponse<>(userService.create(user)));
                }
                return ResponseEntity.badRequest().body(new ApiResponse<>("Same Email already registered!"));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get Users", description = "Get user list from the server (Only Admin)", tags = { "User Management" })
    @GetMapping("/")
    public ResponseEntity<ApiResponse<?>> get(
              @RequestParam(required = false) String email
            , @RequestParam(required = false) Boolean role
            , @RequestParam(required = false) Boolean chat
            , @RequestParam(required = false) String category
            , @RequestParam(required = false) Integer pageNum
            , @RequestParam(required = false) Integer pageLength
            , @RequestParam(required = false) String search
    ) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();

            if(pageNum != null) {
                if(userDetails.getRole().equals(Role.admin))
                    return ResponseEntity.ok(new ApiResponse<>(userService.getPage(pageNum, pageLength, search)));
            } else if( chat != null) {
                if(category != null) {
                    if(!userDetails.getRole().equals(Role.guard) && !userDetails.getRole().equals(Role.client))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersWithRole(userDetails.getId(), category)));
                    else if(userDetails.getRole().equals(Role.guard))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersForGuardsWithRole(userDetails.getId(),category)));
                    else if(userDetails.getRole().equals(Role.client))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersForClientsWithRole(userDetails.getId(),category)));
                }
                else {
                    if(!userDetails.getRole().equals(Role.guard) && !userDetails.getRole().equals(Role.client))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersForStaff(userDetails.getId())));
                    else if(userDetails.getRole().equals(Role.guard))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersForGuards(userDetails.getId())));
                    else if(userDetails.getRole().equals(Role.client))
                        return ResponseEntity.ok(new ApiResponse<>(userService.getChatUsersForClients(userDetails.getId())));
                }
            } else if( role != null) {
                if(userDetails.getRole().equals(Role.client) || userDetails.getRole().equals(Role.admin))
                    return ResponseEntity.ok(new ApiResponse<>(userService.getAreaManagers()));
            } else if( email != null) {
                if(userDetails.getRole().equals(Role.admin)) {
                    return ResponseEntity.ok(new ApiResponse<>(userService.getByEmail(email)));
                }
            } else {
                if(userDetails.getRole().equals((Role.admin)))
                    return ResponseEntity.ok(new ApiResponse<>(userService.getAll()));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Get User", description = "Get user from the server (Only Staff)", tags = { "User Management" })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getOne(
            @PathVariable Integer id
    ) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();

            if(userDetails.getRole().equals(Role.admin) || userDetails.getRole().equals(Role.branch) || userDetails.getRole().equals(Role.area))
                return ResponseEntity.ok(new ApiResponse<>(userService.get(id)));
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this Action"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Delete User", description = "Delete user By User Id (Only Admin)", tags = { "User Management" })
    @DeleteMapping("{id}")
    public ResponseEntity<ApiResponse<?>> removeUser(@PathVariable Integer id) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin)) {
                userService.delete(id);
                return ResponseEntity.ok(new ApiResponse<>(""));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @Operation(summary = "Update User", description = "Update user information from User Id (Only Admin)", tags = { "User Management" })
    @PutMapping("{id}")
    public ResponseEntity<ApiResponse<?>> updateUser(@PathVariable Integer id ,@RequestBody User userDetail) {
        try{
            UserDetailsImpl userDetails = authService.getInfo();
            if(userDetails.getRole().equals(Role.admin) || userDetails.getId().equals(id)) {
                return ResponseEntity.ok(new ApiResponse<>(userService.update(id, userDetail)));
            }
            return ResponseEntity.badRequest().body(new ApiResponse<>("You don't have permission to do this action!"));
        } catch(Exception e) {
            System.out.println(e);
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
}
