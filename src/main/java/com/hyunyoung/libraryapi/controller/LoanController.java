package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoanCreateRequest;
import com.hyunyoung.libraryapi.dto.response.LoanResponse;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import com.hyunyoung.libraryapi.service.LoanService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanCreateRequest request,
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
    public ResponseEntity<Page<LoanResponse>> findMyLoans(Authentication authentication,
                                                          Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<LoanResponse> loans = loanService.findByUserId(userId, pageable)
                .map(LoanResponse::new);
        return ResponseEntity.ok(loans);
    }

    @GetMapping
    public ResponseEntity<Page<LoanResponse>> findAll(Pageable pageable) {
        Page<LoanResponse> loans = loanService.findAll(pageable)
                .map(LoanResponse::new);
        return ResponseEntity.ok(loans);
    }

    @GetMapping("/overdue")
    public ResponseEntity<Page<LoanResponse>> findOverdue(Pageable pageable) {
        Page<LoanResponse> loans = loanService.findOverdue(pageable)
                .map(LoanResponse::new);
        return ResponseEntity.ok(loans);
    }

    private Long getCurrentUserId(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
        return user.getId();
    }
}