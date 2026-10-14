package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminUserUpdateRequest {
    @NotBlank(message = "권한을 설정해주세요.")
    private String authority;
}