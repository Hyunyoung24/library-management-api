package com.hyunyoung.libraryapi.repository;

import com.hyunyoung.libraryapi.entity.Loan;
import com.hyunyoung.libraryapi.enums.LoanStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanRepository extends JpaRepository<Loan, Long> {
    List<Loan> findByUserId(Long userId);

    List<Loan> findByStatusAndDueDateBefore(LoanStatus status, LocalDateTime dateTime);
}