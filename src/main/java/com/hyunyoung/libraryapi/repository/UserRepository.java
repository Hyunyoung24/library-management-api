package com.hyunyoung.libraryapi.repository;

import com.hyunyoung.libraryapi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
