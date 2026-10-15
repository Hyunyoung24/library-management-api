package com.hyunyoung.libraryapi.specification;

import com.hyunyoung.libraryapi.entity.Book;
import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {
    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) ->
                title == null ? null : cb.like(root.get("title"), "%" + title + "%");
    }

    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) ->
                author == null ? null : cb.like(root.get("author"), "%" + author + "%");
    }

    public static Specification<Book> categoryEquals(Long categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("id"), categoryId);
    }
}