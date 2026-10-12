package com.hyunyoung.libraryapi.dto.request;

import lombok.Getter;

@Getter
public class BookCreateRequest {
    private String title;
    private String author;
    private String publisher;
    private String isbn;
    private Long categoryId;
}