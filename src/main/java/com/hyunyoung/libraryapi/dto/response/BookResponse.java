package com.hyunyoung.libraryapi.dto.response;

import com.hyunyoung.libraryapi.entity.Book;
import lombok.Getter;

@Getter
public class BookResponse {
    private final Long id;
    private final String title;
    private final String author;
    private final String publisher;
    private final String isbn;
    private final String categoryName;
    private final String status;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.title = book.getTitle();
        this.author = book.getAuthor();
        this.publisher = book.getPublisher();
        this.isbn = book.getIsbn();
        this.categoryName = book.getCategory() != null
                ? book.getCategory().getName() : null;
        this.status = book.getStatus().name();
    }
}