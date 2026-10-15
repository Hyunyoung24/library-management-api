package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.BookCreateRequest;
import com.hyunyoung.libraryapi.dto.request.BookUpdateRequest;
import com.hyunyoung.libraryapi.dto.response.BookResponse;
import com.hyunyoung.libraryapi.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "도서",
        description = "도서 CRUD API")
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @Operation(
            summary = "도서 목록",
            description = "등록된 도서 목록을 확인합니다. 제목, 저자, 카테고리로 검색할 수 있습니다.")
    @GetMapping
    public ResponseEntity<Page<BookResponse>> findAll(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Long categoryId,
            Pageable pageable) {
        Page<BookResponse> books = bookService.findAll(title, author, categoryId, pageable)
                .map(BookResponse::new);
        return ResponseEntity.ok(books);
    }

    @Operation(
            summary = "도서 조회",
            description = "등록된 특정 도서를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new BookResponse(bookService.findById(id)));
    }

    @Operation(
            summary = "도서 등록",
            description = "새 도서를 등록합니다. 관리자 권한이 필요합니다.")
    @PostMapping
    public ResponseEntity<BookResponse> create(@Valid @RequestBody BookCreateRequest request) {
        BookResponse response = new BookResponse(bookService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(
            summary = "도서 수정",
            description = "등록된 도서 정보를 수정합니다. 관리자 권한이 필요합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id,
                                               @Valid @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(new BookResponse(bookService.update(id, request)));
    }

    @Operation(
            summary = "도서 삭제",
            description = "등록된 도서를 목록에서 삭제합니다. 관리자 권한이 필요합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}