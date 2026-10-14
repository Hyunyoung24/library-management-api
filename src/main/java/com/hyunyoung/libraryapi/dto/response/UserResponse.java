package com.hyunyoung.libraryapi.dto.response;

import com.hyunyoung.libraryapi.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class UserResponse {
    private final Long id;
    private final String username;
    private final String email;
    private final String authority;
    private final LocalDateTime regDate;

    public UserResponse(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.authority = user.getAuthority().name();
        this.regDate = user.getRegDate();
    }
}