package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.AdminUserUpdateRequest;
import com.hyunyoung.libraryapi.dto.request.UserUpdateRequest;
import com.hyunyoung.libraryapi.dto.response.UserResponse;
import com.hyunyoung.libraryapi.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserResponse> getMyInfo(Authentication authentication) {
        return ResponseEntity.ok(
                new UserResponse(userService.findByUsername(authentication.getName())));
    }

    @PatchMapping("/me")
    public ResponseEntity<UserResponse> updateMyInfo(Authentication authentication,
                                                     @Valid @RequestBody UserUpdateRequest request) {
        return ResponseEntity.ok(
                new UserResponse(userService.updateMyInfo(authentication.getName(), request)));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponse>> findAll(Pageable pageable) {
        Page<UserResponse> users = userService.findAll(pageable)
                .map(UserResponse::new);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new UserResponse(userService.findById(id)));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<UserResponse> updateAuthority(@PathVariable Long id,
                                                        @Valid @RequestBody AdminUserUpdateRequest request) {
        return ResponseEntity.ok(new UserResponse(userService.updateAuthority(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }
}