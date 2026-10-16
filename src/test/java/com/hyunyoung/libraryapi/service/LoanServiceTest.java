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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {

    @InjectMocks
    private LoanService loanService;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private UserRepository userRepository;

    private User createUser(Long id) {
        User user = User.create("testuser", "test@test.com", "password");
        ReflectionTestUtils.setField(user, "id", id);
        return user;
    }

    private Book createBook(Long id, BookStatus status) {
        Book book = Book.create("제목", "저자", "출판사", "1234567890", null);
        ReflectionTestUtils.setField(book, "id", id);
        book.updateStatus(status);
        return book;
    }

    @Test
    @DisplayName("대출 신청 - 성공")
    void create_success() {
        // given
        User user = createUser(1L);
        Book book = createBook(1L, BookStatus.AVAILABLE);
        Loan loan = Loan.create(book, user, 14);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(loanRepository.save(any(Loan.class))).willReturn(loan);

        // when
        Loan result = loanService.create(1L, 1L);

        // then
        assertThat(result.getUser()).isEqualTo(user);
        assertThat(result.getBook()).isEqualTo(book);
        assertThat(book.getStatus()).isEqualTo(BookStatus.RENTED);
    }

    @Test
    @DisplayName("대출 신청 - 이미 대출 중인 도서")
    void create_alreadyRented() {
        // given
        User user = createUser(1L);
        Book book = createBook(1L, BookStatus.RENTED);

        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        // when & then
        assertThatThrownBy(() -> loanService.create(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 대출 중인 도서입니다.");
    }

    @Test
    @DisplayName("대출 신청 - 존재하지 않는 사용자")
    void create_userNotFound() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loanService.create(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("대출 신청 - 존재하지 않는 도서")
    void create_bookNotFound() {
        // given
        User user = createUser(1L);
        given(userRepository.findById(1L)).willReturn(Optional.of(user));
        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loanService.create(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("도서를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("반납 처리 - 성공")
    void returnBook_success() {
        // given
        User user = createUser(1L);
        Book book = createBook(1L, BookStatus.RENTED);
        Loan loan = Loan.create(book, user, 14);
        ReflectionTestUtils.setField(loan, "id", 1L);

        given(loanRepository.findById(1L)).willReturn(Optional.of(loan));

        // when
        Loan result = loanService.returnBook(1L, 1L);

        // then
        assertThat(result.getReturnDate()).isNotNull();
        assertThat(result.getStatus()).isEqualTo(LoanStatus.RETURNED);
        assertThat(book.getStatus()).isEqualTo(BookStatus.AVAILABLE);
    }

    @Test
    @DisplayName("반납 처리 - 대출자 불일치")
    void returnBook_userMismatch() {
        // given
        User user = createUser(1L);
        Book book = createBook(1L, BookStatus.RENTED);
        Loan loan = Loan.create(book, user, 14);
        ReflectionTestUtils.setField(loan, "id", 1L);

        given(loanRepository.findById(1L)).willReturn(Optional.of(loan));

        // when & then
        assertThatThrownBy(() -> loanService.returnBook(1L, 2L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("대출자명과 사용자명이 일치하지 않습니다.");
    }

    @Test
    @DisplayName("반납 처리 - 이미 반납된 도서")
    void returnBook_alreadyReturned() {
        // given
        User user = createUser(1L);
        Book book = createBook(1L, BookStatus.RENTED);
        Loan loan = Loan.create(book, user, 14);
        ReflectionTestUtils.setField(loan, "id", 1L);
        loan.returnBook(); // 이미 반납 처리

        given(loanRepository.findById(1L)).willReturn(Optional.of(loan));

        // when & then
        assertThatThrownBy(() -> loanService.returnBook(1L, 1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("이미 반납된 도서입니다.");
    }

    @Test
    @DisplayName("반납 처리 - 존재하지 않는 대출")
    void returnBook_notFound() {
        // given
        given(loanRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> loanService.returnBook(1L, 1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("대출 정보를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자별 대출 내역 조회")
    void findByUserId() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> page = new PageImpl<>(List.of());
        given(loanRepository.findByUserId(1L, pageable)).willReturn(page);

        // when
        Page<Loan> result = loanService.findByUserId(1L, pageable);

        // then
        assertThat(result).isNotNull();
        verify(loanRepository).findByUserId(1L, pageable);
    }

    @Test
    @DisplayName("전체 대출 내역 조회")
    void findAll() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Page<Loan> page = new PageImpl<>(List.of());
        given(loanRepository.findAll(pageable)).willReturn(page);

        // when
        Page<Loan> result = loanService.findAll(pageable);

        // then
        assertThat(result).isNotNull();
        verify(loanRepository).findAll(pageable);
    }
}