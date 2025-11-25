package com.fotomar.authservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fotomar.authservice.dto.LoginRequest;
import com.fotomar.authservice.dto.LoginResponse;
import com.fotomar.authservice.dto.ValidateTokenResponse;
import com.fotomar.authservice.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
    
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        authService.logout(jwtToken);
        return ResponseEntity.ok().build();
    }
    
    @GetMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validateToken(
            @RequestHeader("Authorization") String token) {
        String jwtToken = token.replace("Bearer ", "");
        return ResponseEntity.ok(authService.validateToken(jwtToken));
    }
}