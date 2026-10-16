package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.dto.request.BookCreateRequest;
import com.hyunyoung.libraryapi.dto.request.BookUpdateRequest;
import com.hyunyoung.libraryapi.entity.Book;
import com.hyunyoung.libraryapi.entity.Category;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.BookRepository;
import com.hyunyoung.libraryapi.repository.CategoryRepository;
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
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @InjectMocks
    private BookService bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private BookCreateRequest createBookRequest(String title, String author, String publisher, String isbn,
                                                Long categoryId) {
        BookCreateRequest request = new BookCreateRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "author", author);
        ReflectionTestUtils.setField(request, "publisher", publisher);
        ReflectionTestUtils.setField(request, "isbn", isbn);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        return request;
    }

    @Test
    @DisplayName("도서 목록 조회")
    void findAll() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Book book = Book.create("제목", "저자", "출판사", "1234567890", null);
        Page<Book> page = new PageImpl<>(List.of(book));
        given(bookRepository.findAll(any(Specification.class), eq(pageable))).willReturn(page);

        // when
        Page<Book> result = bookService.findAll(null, null, null, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("도서 상세 조회 - 성공")
    void findById_success() {
        // given
        Book book = Book.create("제목", "저자", "출판사", "1234567890", null);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        // when
        Book result = bookService.findById(1L);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
    }

    @Test
    @DisplayName("도서 상세 조회 - 존재하지 않는 도서")
    void findById_notFound() {
        // given
        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("도서를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("도서 등록 - 카테고리 포함")
    void create_withCategory() {
        // given
        Category category = Category.create("소설");
        Book book = Book.create("제목", "저자", "출판사", "1234567890", category);
        BookCreateRequest request = createBookRequest("제목", "저자", "출판사", "1234567890", 1L);

        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));
        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        Book result = bookService.create(request);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("도서 등록 - 카테고리 없이")
    void create_withoutCategory() {
        // given
        Book book = Book.create("제목", "저자", "출판사", "1234567890", null);
        BookCreateRequest request = createBookRequest("제목", "저자", "출판사", "1234567890", null);

        given(bookRepository.save(any(Book.class))).willReturn(book);

        // when
        Book result = bookService.create(request);

        // then
        assertThat(result.getTitle()).isEqualTo("제목");
        assertThat(result.getCategory()).isNull();
    }

    @Test
    @DisplayName("도서 등록 - 존재하지 않는 카테고리")
    void create_categoryNotFound() {
        // given
        BookCreateRequest request = createBookRequest("제목", "저자", "출판사", "1234567890", 99L);
        given(categoryRepository.findById(99L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.create(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("카테고리를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("도서 삭제")
    void delete() {
        // given
        Book book = Book.create("제목", "저자", "출판사", "1234567890", null);
        given(bookRepository.findById(1L)).willReturn(Optional.of(book));

        // when
        bookService.delete(1L);

        // then
        verify(bookRepository).delete(book);
    }

    private BookUpdateRequest createUpdateRequest(String title, String author, String publisher, Long categoryId) {
        BookUpdateRequest request = new BookUpdateRequest();
        ReflectionTestUtils.setField(request, "title", title);
        ReflectionTestUtils.setField(request, "author", author);
        ReflectionTestUtils.setField(request, "publisher", publisher);
        ReflectionTestUtils.setField(request, "categoryId", categoryId);
        return request;
    }


    @Test
    @DisplayName("도서 수정 - 성공")
    void update_success() {
        // given
        Category category = Category.create("소설");
        Book book = Book.create("원래제목", "원래저자", "원래출판사", "1234567890", null);
        BookUpdateRequest request = createUpdateRequest("수정제목", "수정저자", "수정출판사", 1L);

        given(bookRepository.findById(1L)).willReturn(Optional.of(book));
        given(categoryRepository.findById(1L)).willReturn(Optional.of(category));

        // when
        Book result = bookService.update(1L, request);

        // then
        assertThat(result.getTitle()).isEqualTo("수정제목");
        assertThat(result.getAuthor()).isEqualTo("수정저자");
        assertThat(result.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("도서 수정 - 존재하지 않는 도서")
    void update_notFound() {
        // given
        BookUpdateRequest request = createUpdateRequest("수정제목", "수정저자", "수정출판사", null);
        given(bookRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> bookService.update(1L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("도서를 찾을 수 없습니다.");
    }
}