package com.hyunyoung.libraryapi.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LoanCreateRequest {
    @NotNull(message = "도서 ID를 입력해주세요.")
    private Long bookId;
}
