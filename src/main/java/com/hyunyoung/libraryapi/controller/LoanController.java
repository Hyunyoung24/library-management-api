package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.LoanCreateRequest;
import com.hyunyoung.libraryapi.dto.response.LoanResponse;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import com.hyunyoung.libraryapi.service.LoanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "대출/반납", description = "도서 대출/반납 API")
@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
public class LoanController {

    private final LoanService loanService;
    private final UserRepository userRepository;

    @Operation(
            summary = "도서 대출",
            description = "도서 대출을 신청합니다. 이미 대출 중인 도서에 대해서는 요청이 거부됩니다.")
    @PostMapping
    public ResponseEntity<LoanResponse> create(@Valid @RequestBody LoanCreateRequest request,
                                               Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        LoanResponse response = new LoanResponse(loanService.create(userId, request.getBookId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "도서 반납",
            description = "대출한 도서를 반납합니다.")
    @PatchMapping("/{id}/return")
    public ResponseEntity<LoanResponse> returnBook(@PathVariable Long id,
                                                   Authentication authentication) {
        Long userId = getCurrentUserId(authentication);
        return ResponseEntity.ok(new LoanResponse(loanService.returnBook(id, userId)));
    }

    @Operation(
            summary = "대출 내역 조회",
            description = "자신의 도서 대출 내역을 확인합니다.")
    @GetMapping("/me")
    public ResponseEntity<Page<LoanResponse>> findMyLoans(Authentication authentication,
                                                          Pageable pageable) {
        Long userId = getCurrentUserId(authentication);
        Page<LoanResponse> loans = loanService.findByUserId(userId, pageable)
                .map(LoanResponse::new);
        return ResponseEntity.ok(loans);
    }

    @Operation(
            summary = "전체 대출 내역 조회",
            description = "모든 도서 대출 내역을 확인합니다. 관리자 권한이 필요합니다.")
    @GetMapping
    public ResponseEntity<Page<LoanResponse>> findAll(Pageable pageable) {
        Page<LoanResponse> loans = loanService.findAll(pageable)
                .map(LoanResponse::new);
        return ResponseEntity.ok(loans);
    }

    @Operation(
            summary = "전체 연체 내역 조회",
            description = "모든 도서 연체 내역을 확인합니다. 관리자 권한이 필요합니다.")
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