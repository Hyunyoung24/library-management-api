package com.hyunyoung.libraryapi.dto.request;

import lombok.Getter;

@Getter
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
}