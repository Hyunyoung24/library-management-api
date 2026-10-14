package com.hyunyoung.libraryapi.controller;

import tools.jackson.databind.ObjectMapper;
import com.hyunyoung.libraryapi.config.JwtTokenProvider;
import com.hyunyoung.libraryapi.entity.Book;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.repository.BookRepository;
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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LoanControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String userToken;
    private String adminToken;
    private User testUser;
    private Book testBook;

    @BeforeEach
    void setUp() {
        // 테스트 사용자 생성
        testUser = User.create("testuser", "test@example.com",
                passwordEncoder.encode("password123"));
        userRepository.save(testUser);

        // 테스트 도서 생성
        testBook = Book.create("테스트 도서", "테스트 저자", "테스트 출판사",
                "978-89-0000-001-0", null);
        bookRepository.save(testBook);

        // 토큰 생성
        userToken = jwtTokenProvider.createToken("testuser", "USER");
        adminToken = jwtTokenProvider.createToken("testuser", "ADMIN");
    }

    private Long createLoanAndGetId() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "bookId", testBook.getId()
        ));

        MvcResult result = mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn();

        return objectMapper.readTree(result.getResponse().getContentAsString())
                .get("id").asLong();
    }

    @Test
    @DisplayName("대출 생성 성공")
    void create_success() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "bookId", testBook.getId()
        ));

        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("RENTED"))
                .andDo(print());
    }

    @Test
    @DisplayName("대출 생성 실패 - 인증 없음")
    void create_fail_noAuth() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "bookId", testBook.getId()
        ));

        mockMvc.perform(post("/api/loans")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("반납 성공")
    void returnBook_success() throws Exception {
        Long loanId = createLoanAndGetId();

        mockMvc.perform(patch("/api/loans/{id}/return", loanId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("RETURNED"))
                .andExpect(jsonPath("$.returnDate").exists())
                .andDo(print());
    }

    @Test
    @DisplayName("내 대출 목록 조회")
    void findMyLoans_success() throws Exception {
        createLoanAndGetId();

        mockMvc.perform(get("/api/loans/me")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 대출 목록 조회 - ADMIN")
    void findAll_success_admin() throws Exception {
        createLoanAndGetId();

        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andDo(print());
    }

    @Test
    @DisplayName("전체 대출 목록 조회 실패 - USER 권한")
    void findAll_fail_userRole() throws Exception {
        mockMvc.perform(get("/api/loans")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("연체 목록 조회 - ADMIN")
    void findOverdue_success_admin() throws Exception {
        mockMvc.perform(get("/api/loans/overdue")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("연체 목록 조회 실패 - USER 권한")
    void findOverdue_fail_userRole() throws Exception {
        mockMvc.perform(get("/api/loans/overdue")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    // 입력값 검증 실패 테스트 케이스

    @Test
    @DisplayName("대출 생성 실패 - 도서 ID 누락")
    void create_fail_nullBookId() throws Exception {
        mockMvc.perform(post("/api/loans")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}