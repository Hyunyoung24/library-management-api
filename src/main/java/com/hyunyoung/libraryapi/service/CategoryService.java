package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.entity.Category;
import com.hyunyoung.libraryapi.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category create(String name) {
        return categoryRepository.save(Category.create(name));
    }
}