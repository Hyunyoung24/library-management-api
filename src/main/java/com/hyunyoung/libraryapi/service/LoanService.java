package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.entity.Book;
import com.hyunyoung.libraryapi.entity.Loan;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.enums.BookStatus;
import com.hyunyoung.libraryapi.enums.LoanStatus;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.BookRepository;
import com.hyunyoung.libraryapi.repository.LoanRepository;
import com.hyunyoung.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LoanService {

    private static final int LOAN_DAYS = 14;
    private final LoanRepository loanRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;

    @Transactional
    public Loan create(Long userId, Long bookId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));

        Book book = bookRepository.findById(bookId).orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다."));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("이미 대출 중인 도서입니다.");
        }

        book.updateStatus(BookStatus.RENTED);
        Loan loan = Loan.create(book, user, LOAN_DAYS);

        return loanRepository.save(loan);
    }

    @Transactional
    public Loan returnBook(Long loanId, Long userId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new ResourceNotFoundException("대출 정보를 찾을 수 없습니다."));

        if (!loan.getUser().getId().equals(userId)) {
            throw new IllegalStateException("대출자명과 사용자명이 일치하지 않습니다.");
        }

        if (loan.getReturnDate() != null) {
            throw new IllegalStateException("이미 반납된 도서입니다.");
        }

        loan.returnBook();
        loan.getBook().updateStatus(BookStatus.AVAILABLE);

        return loan;
    }

    public Page<Loan> findByUserId(Long userId, Pageable pageable) {
        return loanRepository.findByUserId(userId, pageable);
    }

    public Page<Loan> findAll(Pageable pageable) {
        return loanRepository.findAll(pageable);
    }

    public Page<Loan> findOverdue(Pageable pageable) {
        return loanRepository.findByStatusAndDueDateBefore(LoanStatus.RENTED, LocalDateTime.now(), pageable);
    }
}
