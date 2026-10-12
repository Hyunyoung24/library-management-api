package com.hyunyoung.libraryapi.entity;

import com.hyunyoung.libraryapi.enums.Authority;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public static User create(String username, String email, String password) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = password;
        user.regDate = LocalDateTime.now();
        return user;
    }

    public void updateInfo(String email) {
        this.email = email;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateAuthority(Authority authority) {
        this.authority = authority;
    }
}