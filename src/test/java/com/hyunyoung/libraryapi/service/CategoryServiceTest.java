package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.entity.Category;
import com.hyunyoung.libraryapi.repository.CategoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    private CategoryService categoryService;

    @Mock
    private CategoryRepository categoryRepository;

    @Test
    @DisplayName("카테고리 목록 조회")
    void findAll() {
        // given
        Category category1 = Category.create("소설");
        Category category2 = Category.create("과학");
        given(categoryRepository.findAll()).willReturn(List.of(category1, category2));

        // when
        List<Category> result = categoryService.findAll();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getName()).isEqualTo("소설");
        assertThat(result.get(1).getName()).isEqualTo("과학");
        verify(categoryRepository).findAll();
    }

    @Test
    @DisplayName("카테고리 등록")
    void create() {
        // given
        Category category = Category.create("소설");
        given(categoryRepository.save(any(Category.class))).willReturn(category);

        // when
        Category result = categoryService.create("소설");

        // then
        assertThat(result.getName()).isEqualTo("소설");
        verify(categoryRepository).save(any(Category.class));
    }
}