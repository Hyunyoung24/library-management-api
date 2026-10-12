package com.hyunyoung.libraryapi.entity;

import com.hyunyoung.libraryapi.enums.BookStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "books")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;

    @Column(nullable = false)
    private String publisher;

    @Column(length = 20, nullable = false, unique = true)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private BookStatus status = BookStatus.AVAILABLE;

    public static Book create(String title, String author, String publisher,
                              String isbn, Category category) {
        Book book = new Book();
        book.title = title;
        book.author = author;
        book.publisher = publisher;
        book.isbn = isbn;
        book.category = category;
        return book;
    }

    public void update(String title, String author, String publisher,
                       Category category) {
        this.title = title;
        this.author = author;
        this.publisher = publisher;
        this.category = category;
    }

    public void updateStatus(BookStatus status) {
        this.status = status;
    }
}