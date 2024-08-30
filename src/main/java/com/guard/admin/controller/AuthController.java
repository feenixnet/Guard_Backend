package com.guard.admin.controller;

import com.guard.admin.database.entities.Client;
import com.guard.admin.database.entities.Guard;
import com.guard.admin.database.repositories.ClientRepository;
import com.guard.admin.database.repositories.GuardRepository;
import com.guard.admin.payload.request.TokenRequest;
import com.guard.admin.payload.response.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.guard.admin.database.entities.User;
import com.guard.admin.payload.request.LoginRequest;
import com.guard.admin.database.repositories.UserRepository;
import com.guard.admin.utils.jwt.JwtUtils;
import com.guard.admin.service.impl.UserDetailsImpl;
import com.guard.admin.utils.jwt.JwtTokenParser;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("api/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;
  
    @Autowired
    UserRepository userRepository;

    @Autowired
    GuardRepository guardRepository;

    @Autowired
    ClientRepository clientRepository;
  
    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    JwtTokenParser jwtTokenParser;

    @PostMapping("staff/signin")
    public ResponseEntity<ApiResponse<?>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try{

            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            User user = userRepository.findById(userDetails.getId()).get();

            return ResponseEntity.ok(new ApiResponse<>(new JwtStaffResponse(jwt, refreshToken, user)));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("staff/me")
    public ResponseEntity<ApiResponse<?>> loginWithToken(HttpServletRequest request) {
        try{

            String accessToken = jwtTokenParser.parseJwt(request);
            String email = jwtUtils.getEmailFromJwtToken(accessToken);
            if (userRepository.existsByEmail(email)) {
                User currentUser = userRepository.findByEmail(email).get();
                return ResponseEntity.ok(new ApiResponse<>(currentUser));
            }
            else
                return ResponseEntity
                .badRequest()
                .body(new ApiResponse<>("Error: Invalid Token !"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("staff/refresh-token")
    public ResponseEntity<ApiResponse<?>> refresh(@RequestBody TokenRequest tokenRequest) {
        try{

            String accessToken = tokenRequest.getToken();

            String email = jwtUtils.getEmailFromJwtToken(accessToken);

            if (userRepository.existsByEmail(email)) {

                User currentUser = userRepository.findByEmail(email).get();

                return ResponseEntity.ok(new ApiResponse<>(new TokenResponse(jwtUtils.generateAccessToken(currentUser.getEmail()))));
            }
            else
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("Error: Invalid Token !"));
        } catch(Exception e) {
        return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("guard/signin")
    public ResponseEntity<ApiResponse<?>> authenticateGuard(@Valid @RequestBody LoginRequest loginRequest) {

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Guard guard = guardRepository.findById(userDetails.getId()).get();

        
            System.out.println(guard.getEmail());

            return ResponseEntity.ok(new ApiResponse<>(new JwtGuardResponse(jwt, refreshToken, guard)));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("guard/me")
    public ResponseEntity<ApiResponse<?>> loginWithGuardToken(HttpServletRequest request) {

        try{
            String accessToken = jwtTokenParser.parseJwt(request);
            String email = jwtUtils.getEmailFromJwtToken(accessToken);
            if (guardRepository.existsByEmail(email)) {
                Guard currentUser = guardRepository.findByEmail(email).get();
                return ResponseEntity.ok(new ApiResponse<>(currentUser));
            }
            else
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("Error: Invalid Token !"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("guard/refresh-token")
    public ResponseEntity<ApiResponse<?>> guardRefresh(@RequestBody TokenRequest tokenRequest) {

        try{
            String accessToken = tokenRequest.getToken();

            String email = jwtUtils.getEmailFromJwtToken(accessToken);

            if (guardRepository.existsByEmail(email)) {
                Guard currentGuard = guardRepository.findByEmail(email).get();
                return ResponseEntity.ok(new ApiResponse<>(new TokenResponse(jwtUtils.generateAccessToken(currentGuard.getEmail()))));
            }
            else
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("Error: Invalid Token !"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("client/signin")
    public ResponseEntity<ApiResponse<?>> authenticateClient(@Valid @RequestBody LoginRequest loginRequest) {

        try{
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);
            String refreshToken = jwtUtils.generateRefreshToken(authentication);

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            Client client = clientRepository.findById(userDetails.getId()).get();

            return ResponseEntity.ok(new ApiResponse<>(new JwtClientResponse(jwt, refreshToken, client)));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("client/me")
    public ResponseEntity<ApiResponse<?>> loginWithClientToken(HttpServletRequest request) {
        try{
            String accessToken = jwtTokenParser.parseJwt(request);

            String email = jwtUtils.getEmailFromJwtToken(accessToken);

            if (clientRepository.existsByEmail(email)) {

                Client currentUser = clientRepository.findByEmail(email).get();

                return ResponseEntity.ok(new ApiResponse<>(currentUser));
            }
            else
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("Error: Invalid Token !"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }

    @PostMapping("client/refresh-token")
    public ResponseEntity<ApiResponse<?>> clientRefresh(@RequestBody TokenRequest tokenRequest) {

        try{
            String accessToken = tokenRequest.getToken();

            String email = jwtUtils.getEmailFromJwtToken(accessToken);

            if (clientRepository.existsByEmail(email)) {
                Client currentClient = clientRepository.findByEmail(email).get();
                return ResponseEntity.ok(new ApiResponse<>(new TokenResponse(jwtUtils.generateAccessToken(currentClient.getEmail()))));
            }
            else
                return ResponseEntity
                        .badRequest()
                        .body(new ApiResponse<>("Invalid Token !"));
        } catch(Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(e.getMessage()));}
    }
}
