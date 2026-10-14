package com.hyunyoung.libraryapi.controller;

import tools.jackson.databind.ObjectMapper;
import com.hyunyoung.libraryapi.config.JwtTokenProvider;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;
    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.create("testuser", "test@example.com",
                passwordEncoder.encode("password123"));
        userRepository.save(testUser);

        userToken = jwtTokenProvider.createToken("testuser", "USER");
        adminToken = jwtTokenProvider.createToken("testuser", "ADMIN");
    }

    // === 내 정보 ===

    @Test
    @DisplayName("내 정보 조회 성공")
    void getMyInfo_success() throws Exception {
        mockMvc.perform(get("/api/users/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 조회 실패 - 인증 없음")
    void getMyInfo_fail_noAuth() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 수정 - 이메일 변경")
    void updateMyInfo_email() throws Exception {
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("email", "new@example.com");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 수정 - 비밀번호 변경")
    void updateMyInfo_password() throws Exception {
        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("password", "newpassword123");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("내 정보 수정 실패 - 중복 이메일")
    void updateMyInfo_fail_duplicateEmail() throws Exception {
        // 다른 사용자 생성
        User otherUser = User.create("otheruser", "other@example.com",
                passwordEncoder.encode("password123"));
        userRepository.save(otherUser);

        Map<String, String> updateMap = new HashMap<>();
        updateMap.put("email", "other@example.com");

        mockMvc.perform(patch("/api/users/me")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    // === 관리자 기능 ===

    @Test
    @DisplayName("전체 사용자 조회 - ADMIN")
    void findAll_success_admin() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 사용자 조회 실패 - USER 권한")
    void findAll_fail_userRole() throws Exception {
        mockMvc.perform(get("/api/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 단건 조회 - ADMIN")
    void findById_success_admin() throws Exception {
        mockMvc.perform(get("/api/users/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andDo(print());
    }

    @Test
    @DisplayName("권한 변경 - ADMIN")
    void updateAuthority_success_admin() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "authority", "ADMIN"
        ));

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.authority").value("ADMIN"))
                .andDo(print());
    }

    @Test
    @DisplayName("권한 변경 실패 - USER 권한")
    void updateAuthority_fail_userRole() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "authority", "ADMIN"
        ));

        mockMvc.perform(patch("/api/users/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 삭제 - ADMIN")
    void delete_success_admin() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 삭제 실패 - USER 권한")
    void delete_fail_userRole() throws Exception {
        mockMvc.perform(delete("/api/users/{id}", testUser.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andDo(print());
    }
}