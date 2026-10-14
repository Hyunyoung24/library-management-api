package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class BookCreateRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "저자를 입력해주세요.")
    private String author;

    @NotBlank(message = "출판사를 입력해주세요.")
    private String publisher;

    @NotBlank(message = "ISBN을 입력해주세요.")
    private String isbn;
    private Long categoryId;
}