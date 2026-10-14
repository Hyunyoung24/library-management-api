package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoginRequest;
import com.hyunyoung.libraryapi.dto.request.RegisterRequest;
import com.hyunyoung.libraryapi.dto.response.TokenResponse;
import com.hyunyoung.libraryapi.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}