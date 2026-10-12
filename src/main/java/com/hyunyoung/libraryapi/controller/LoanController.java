package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoanCreateRequest;
import com.hyunyoung.libraryapi.dto.response.LoanResponse;
import com.hyunyoung.libraryapi.service.LoanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;

    @PostMapping
    public ResponseEntity<LoanResponse> create(@RequestBody LoanCreateRequest request) {
        // TODO: Spring Security 적용 후 인증된 UID로 교체
        Long userId = 1L;
        LoanResponse response = new LoanResponse(loanService.create(userId, request.getBookId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id) {
        // TODO: Spring Security 적용 후 인증된 UID로 교체
        Long userId = 1L;
        return ResponseEntity.ok(new LoanResponse(loanService.returnBook(id, userId)));
    }

    @GetMapping("/me")
    public ResponseEntity<List<LoanResponse>> findMyLoans() {
        // TODO: Spring Security 적용 후 인증된 UID로 교체
        Long userId = 1L;
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
}
