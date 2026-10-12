package com.hyunyoung.libraryapi.dto.response;

import com.hyunyoung.libraryapi.entity.Category;
import lombok.Getter;

@Getter
public class CategoryResponse {
    private final Long id;
    private final String name;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}