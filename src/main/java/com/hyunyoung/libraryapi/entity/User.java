package com.hyunyoung.libraryapi.entity;

import com.hyunyoung.libraryapi.enums.Authority;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private Authority authority = Authority.USER;

    @Column(nullable = false, updatable = false)
    private LocalDateTime regDate;
}