package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.BookCreateRequest;
import com.hyunyoung.libraryapi.dto.request.BookUpdateRequest;
import com.hyunyoung.libraryapi.dto.response.BookResponse;
import com.hyunyoung.libraryapi.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<List<BookResponse>> findAll() {
        List<BookResponse> books = bookService.findAll()
                .stream()
                .map(BookResponse::new)
                .toList();
        return ResponseEntity.ok(books);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(new BookResponse(bookService.findById(id)));
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(@RequestBody BookCreateRequest request) {
        BookResponse response = new BookResponse(bookService.create(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> update(@PathVariable Long id,
                                               @RequestBody BookUpdateRequest request) {
        return ResponseEntity.ok(new BookResponse(bookService.update(id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        bookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}