package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.AdminUserUpdateRequest;
import com.hyunyoung.libraryapi.dto.request.UserUpdateRequest;
import com.hyunyoung.libraryapi.dto.response.UserResponse;
import com.hyunyoung.libraryapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "사용자",
        description = "사용자 CRUD API")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @Operation(
            summary = "내 정보 조회",
            description = "자신의 계정 정보를 확인합니다.")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        return ResponseEntity.ok(
                new UserResponse(userService.findByUsername(authentication.getName())));
    }

    @Operation(
            summary = "내 정보 수정",
            description = "자신의 계정 정보를 수정합니다.")
    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(Authentication authentication,
                                                     @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(
                new UserResponse(userService.updateMyInfo(authentication.getName(), request)));
    }

    @Operation(
            summary = "모든 계정 조회",
            description = "등록된 모든 계정 목록을 확인합니다.")
    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        Page<UserResponse> users = userService.findAll(pageable)
                .map(UserResponse::new);
        return ResponseEntity.ok(users);
    }

    @Operation(
            summary = "계정 조회",
            description = "특정 계정의 정보를 확인합니다. 관리자 권한이 필요합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponse(userService.findById(id)));
    }

    @Operation(
            summary = "권한 변경",
            description = "특정 계정의 권한을 변경합니다. 관리자 권한이 필요합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateAuthority(@PathVariable Long id,
                                                        @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(new UserResponse(userService.updateAuthority(id, request)));
    }

    @Operation(
            summary = "계정 삭제",
            description = "특정 계정을 삭제합니다. 관리자 권한이 필요합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}