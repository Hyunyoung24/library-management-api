package com.hyunyoung.libraryapi.repository;

import com.hyunyoung.libraryapi.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}