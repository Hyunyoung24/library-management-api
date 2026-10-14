package com.hyunyoung.libraryapi.controller;

import tools.jackson.databind.ObjectMapper;
import com.hyunyoung.libraryapi.config.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        adminToken = jwtTokenProvider.createToken("admin", "ADMIN");
        userToken = jwtTokenProvider.createToken("testuser", "USER");
    }

    @Test
    @DisplayName("카테고리 등록 성공 - ADMIN")
    void create_success_admin() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "name", "소설"
        ));

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("소설"))
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 등록 실패 - USER 권한")
    void create_fail_userRole() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "name", "소설"
        ));

        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 등록 실패 - 인증 없음")
    void create_fail_noAuth() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "name", "소설"
        ));

        mockMvc.perform(post("/api/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("카테고리 전체 조회 - 인증 없이 가능")
    void findAll_success_noAuth() throws Exception {
        // 카테고리 2개 등록
        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "소설"))));

        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("name", "과학"))));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    // 입력값 검증 실패 테스트 케이스

    @Test
    @DisplayName("카테고리 등록 실패 - 카테고리명 누락")
    void create_fail_blankName() throws Exception {
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}