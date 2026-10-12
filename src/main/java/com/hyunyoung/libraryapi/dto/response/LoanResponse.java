package com.hyunyoung.libraryapi.dto.response;

import com.hyunyoung.libraryapi.entity.Loan;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LoanResponse {
    private final Long id;
    private final String bookTitle;
    private final String username;
    private final LocalDateTime loanDate;
    private final LocalDateTime dueDate;
    private final LocalDateTime returnDate;
    private final String status;
    private final boolean overdue;

    public LoanResponse(Loan loan) {
        this.id = loan.getId();
        this.bookTitle = loan.getBook().getTitle();
        this.username = loan.getUser().getUsername();
        this.loanDate = loan.getLoanDate();
        this.dueDate = loan.getDueDate();
        this.returnDate = loan.getReturnDate();
        this.status = loan.getStatus().name();
        this.overdue = loan.getReturnDate() == null && loan.getDueDate().isBefore(LocalDateTime.now());
    }
}
