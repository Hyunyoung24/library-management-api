package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoginRequest;
import com.hyunyoung.libraryapi.dto.request.RegisterRequest;
import com.hyunyoung.libraryapi.dto.response.TokenResponse;
import com.hyunyoung.libraryapi.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "인증",
        description = "회원가입/로그인 API")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(
            summary = "회원가입",
            description = """
                    사용자 정보(사용자명, 이메일, 비밀번호)를 입력받아 새로운 회원을 등록합니다.
                    사용자명은 2자리 이상, 비밀번호는 8~16자리여야 합니다.
                    이미 등록된 이메일로는 가입이 불가능합니다.""")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "로그인",
            description = "등록된 사용자명과 비밀번호로 로그인합니다."
    )
    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new TokenResponse(token));
    }
}