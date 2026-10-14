package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.dto.request.BookCreateRequest;
import com.hyunyoung.libraryapi.dto.request.BookUpdateRequest;
import com.hyunyoung.libraryapi.entity.Book;
import com.hyunyoung.libraryapi.entity.Category;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.BookRepository;
import com.hyunyoung.libraryapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryRepository categoryRepository;

    public List<Book> findAll() {
        return bookRepository.findAll();
    }

    public Book findById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("도서를 찾을 수 없습니다."));
    }

    @Transactional
    public Book create(BookCreateRequest request) {
        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
        }

        Book book = Book.create(
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                request.getIsbn(),
                category
        );

        return bookRepository.save(book);
    }

    @Transactional
    public Book update(Long id, BookUpdateRequest request) {
        Book book = findById(id);

        Category category = null;
        if (request.getCategoryId() != null) {
            category = categoryRepository.findById(request.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("카테고리를 찾을 수 없습니다."));
        }

        book.update(
                request.getTitle(),
                request.getAuthor(),
                request.getPublisher(),
                category
        );

        return book;
    }

    @Transactional
    public void delete(Long id) {
        Book book = findById(id);
        bookRepository.delete(book);
    }
}