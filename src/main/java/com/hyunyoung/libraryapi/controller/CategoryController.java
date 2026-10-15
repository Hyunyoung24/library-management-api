package com.hyunyoung.libraryapi.controller;

import com.hyunyoung.libraryapi.dto.request.CategoryCreateRequest;
import com.hyunyoung.libraryapi.dto.response.CategoryResponse;
import com.hyunyoung.libraryapi.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "카테고리", description = "카테고리 등록/조회 API")
@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @Operation(
            summary = "카테고리 조회",
            description = "등록된 카테고리 목록을 확인합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> findAll() {
        List<CategoryResponse> categories = categoryService.findAll()
                .stream()
                .map(CategoryResponse::new)
                .toList();
        return ResponseEntity.ok(categories);
    }

    @Operation(
            summary = "카테고리 추가",
            description = "새로운 카테고리를 등록합니다. 관리자 권한이 필요합니다.")
    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryCreateRequest request) {
        CategoryResponse response = new CategoryResponse(categoryService.create(request.getName()));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}