package com.hyunyoung.libraryapi.controller;

import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void register_success() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 사용자명")
    void register_fail_duplicateUsername() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        // 첫 번째 가입
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        // 같은 사용자명으로 재가입
        String duplicateJson = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "other@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void register_fail_duplicateEmail() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        String duplicateJson = objectMapper.writeValueAsString(Map.of(
                "username", "otheruser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(duplicateJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() throws Exception {
        // 회원가입
        String registerJson = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated());

        // 로그인
        String loginJson = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_userNotFound() throws Exception {
        String loginJson = objectMapper.writeValueAsString(Map.of(
                "username", "nonexistent",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isNotFound())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_wrongPassword() throws Exception {
        // 회원가입
        String registerJson = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerJson))
                .andExpect(status().isCreated());

        // 틀린 비밀번호로 로그인
        String loginJson = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "password", "wrongpassword"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // 입력값 검증 실패 테스트 케이스

    @Test
    @DisplayName("회원가입 실패 - 사용자명 누락")
    void register_fail_blankUsername() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "email", "test@example.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 잘못된 이메일 형식")
    void register_fail_invalidEmail() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "testexample.com",
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입 실패 - 비밀번호 길이 부족")
    void register_fail_shortPassword() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "username", "testuser",
                "email", "test@example.com",
                "password", "shortpw"
        ));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 실패 - 사용자명 누락")
    void login_fail_blankUsername() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "password", "password123"
        ));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}