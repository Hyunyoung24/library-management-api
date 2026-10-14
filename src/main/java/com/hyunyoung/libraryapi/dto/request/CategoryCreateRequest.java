package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class CategoryCreateRequest {
    @NotBlank(message = "카테고리명을 입력해주세요.")
    private String name;
}