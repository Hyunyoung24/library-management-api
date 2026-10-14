package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class RegisterRequest {
    @NotBlank(message = "사용자명을 입력해주세요.")
    @Size(min = 2, max = 20, message = "사용자명은 2~20자여야 합니다.")
    private String username;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message= "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, max = 16, message = "비밀번호는 8~16자여야 합니다.")
    private String password;
}