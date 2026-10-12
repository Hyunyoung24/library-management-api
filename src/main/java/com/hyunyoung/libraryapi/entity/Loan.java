package com.hyunyoung.libraryapi.entity;

import com.hyunyoung.libraryapi.enums.LoanStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "loans")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Loan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime loanDate;

    @Column(nullable = false)
    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private LoanStatus status = LoanStatus.RENTED;

    public static Loan create(Book book, User user, int loanDays) {
        Loan loan = new Loan();
        loan.book = book;
        loan.user = user;
        loan.loanDate = LocalDateTime.now();
        loan.dueDate = loan.loanDate.plusDays(loanDays);
        return loan;
    }

    public void returnBook() {
        this.returnDate = LocalDateTime.now();
        this.status = LoanStatus.RETURNED;
    }
}