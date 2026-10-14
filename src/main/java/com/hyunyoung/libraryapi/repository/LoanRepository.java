package com.hyunyoung.libraryapi.repository;

import com.hyunyoung.libraryapi.entity.Loan;
import com.hyunyoung.libraryapi.enums.LoanStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    Page<Loan> findByUserId(Long userId, Pageable pageable);

    Page<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime dateTime, Pageable pageable);
}