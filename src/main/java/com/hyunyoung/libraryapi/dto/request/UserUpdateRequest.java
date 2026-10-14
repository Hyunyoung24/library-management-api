package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UserUpdateRequest {
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @Size(min = 8, max = 16, message = "비밀번호는 8~16자여야 합니다.")
    private String password;
}