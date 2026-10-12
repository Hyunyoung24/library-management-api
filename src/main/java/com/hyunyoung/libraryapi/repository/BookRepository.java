package com.hyunyoung.libraryapi.repository;

import com.hyunyoung.libraryapi.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}