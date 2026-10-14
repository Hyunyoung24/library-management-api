package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.CategoryCreateRequest;
import com.hyunyoung.libraryapi.dto.response.CategoryResponse;
import com.hyunyoung.libraryapi.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<CategoryResponse> categories = categoryService.findAll()
                .stream()
                .map(CategoryResponse::new)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = new CategoryResponse(categoryService.create(request.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}