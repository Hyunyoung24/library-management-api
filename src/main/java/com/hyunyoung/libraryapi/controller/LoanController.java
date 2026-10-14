package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoanCreateRequest;
import com.hyunyoung.libraryapi.dto.response.LoanResponse;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import com.hyunyoung.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<LoanResponse> create(@RequestBody LoanCreateRequest request,
                                               Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        LoanResponse response = new LoanResponse(loanService.create(userId, request.getBookId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id,
                                                   Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(new LoanResponse(loanService.returnBook(id, userId)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<LoanResponse>> findMyLoans(Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        List<LoanResponse> loans = loanService
                .findByUserId(userId)
                .stream()
                .map(LoanResponse::new)
                .toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping
    public ResponseEntity<List<LoanResponse>> findAll() {
        List<LoanResponse> loans = loanService
                .findAll()
                .stream()
                .map(LoanResponse::new)
                .toList();
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<LoanResponse>> findOverdue() {
        List<LoanResponse> loans = loanService
                .findOverdue()
                .stream()
                .map(LoanResponse::new)
                .toList();
        return ResponseEntity.ok(loans);
    }

    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }
}