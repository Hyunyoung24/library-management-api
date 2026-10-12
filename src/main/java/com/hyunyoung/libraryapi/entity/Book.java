package com.hyunyoung.libraryapi.entity;

import com.hyunyoung.libraryapi.enums.BookStatus;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "books")
@Getter
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
}