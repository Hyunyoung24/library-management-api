package com.hyunyoung.libraryapi.dto.request;

import lombok.Getter;

@Getter
public class BookUpdateRequest {
    private String title;
    private String author;
    private String publisher;
    private Long categoryId;
}