package com.hyunyoung.libraryapi.controller;

import tools.jackson.databind.ObjectMapper;
import com.hyunyoung.libraryapi.config.JwtTokenProvider;
import com.hyunyoung.libraryapi.entity.Category;
import com.hyunyoung.libraryapi.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private CategoryRepository categoryRepository;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() throws Exception {
        // ADMIN 토큰 생성
        adminToken = jwtTokenProvider.createToken("admin", "ADMIN");

        // USER 토큰 생성
        userToken = jwtTokenProvider.createToken("testuser", "USER");
    }

    private Long createBookAndGetId(String title, String isbn) throws Exception {
        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("title", title);
        bookMap.put("author", "테스트 저자");
        bookMap.put("publisher", "테스트 출판사");
        bookMap.put("isbn", isbn);

        MvcResult result = mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMap)))
                .andExpect(status().isCreated())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    @DisplayName("도서 등록 성공 - ADMIN")
    void create_success_admin() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "title", "테스트 도서",
                "author", "테스트 저자",
                "publisher", "테스트 출판사",
                "isbn", "978-89-1234-567-0"
        ));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 도서"))
                .andExpect(jsonPath("$.author").value("테스트 저자"))
                .andExpect(jsonPath("$.isbn").value("978-89-1234-567-0"))
                .andExpect(jsonPath("$.status").value("AVAILABLE"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 등록 성공 - 카테고리 포함")
    void create_success_withCategory() throws Exception {
        Category category = categoryRepository.save(Category.create("소설"));

        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("title", "테스트 도서");
        bookMap.put("author", "테스트 저자");
        bookMap.put("publisher", "테스트 출판사");
        bookMap.put("isbn", "978-89-1234-567-0");
        bookMap.put("categoryId", category.getId());

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMap)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 도서"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 등록 실패 - USER 권한")
    void create_fail_userRole() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "title", "테스트 도서",
                "author", "테스트 저자",
                "publisher", "테스트 출판사",
                "isbn", "978-89-1234-567-0"
        ));

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 등록 실패 - 인증 없음")
    void create_fail_noAuth() throws Exception {
        String json = objectMapper.writeValueAsString(Map.of(
                "title", "테스트 도서",
                "author", "테스트 저자",
                "publisher", "테스트 출판사",
                "isbn", "978-89-1234-567-0"
        ));

        mockMvc.perform(post("/api/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 전체 조회 - 인증 없이 가능")
    void findAll_success_noAuth() throws Exception {
        createBookAndGetId("도서1", "978-89-0000-001-0");
        createBookAndGetId("도서2", "978-89-0000-002-0");

        mockMvc.perform(get("/api/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 단건 조회")
    void findById_success() throws Exception {
        Long bookId = createBookAndGetId("테스트 도서", "978-89-0000-001-0");

        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("테스트 도서"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 수정 성공 - ADMIN")
    void update_success_admin() throws Exception {
        Long bookId = createBookAndGetId("원래 제목", "978-89-0000-001-0");

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", "수정된 제목");
        updateMap.put("author", "수정된 저자");
        updateMap.put("publisher", "수정된 출판사");

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"))
                .andExpect(jsonPath("$.author").value("수정된 저자"))
                .andDo(print());
    }

    @Test
    @DisplayName("도서 수정 실패 - USER 권한")
    void update_fail_userRole() throws Exception {
        Long bookId = createBookAndGetId("원래 제목", "978-89-0000-001-0");

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", "수정된 제목");
        updateMap.put("author", "수정된 저자");
        updateMap.put("publisher", "수정된 출판사");

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 삭제 성공 - ADMIN")
    void delete_success_admin() throws Exception {
        Long bookId = createBookAndGetId("삭제할 도서", "978-89-0000-001-0");

        mockMvc.perform(delete("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent())
                .andDo(print());

        // 삭제 후 조회 시 에러
        mockMvc.perform(get("/api/books/{id}", bookId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("도서 삭제 실패 - USER 권한")
    void delete_fail_userRole() throws Exception {
        Long bookId = createBookAndGetId("삭제할 도서", "978-89-0000-001-0");

        mockMvc.perform(delete("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden())
                .andDo(print());
    }

    // 입력값 검증 실패 테스트 케이스

    @Test
    @DisplayName("도서 등록 실패 - 제목 누락")
    void create_fail_blankTitle() throws Exception {
        Map<String, Object> bookMap = new HashMap<>();
        bookMap.put("title", "");
        bookMap.put("author", "테스트 저자");
        bookMap.put("publisher", "테스트 출판사");
        bookMap.put("isbn", "978-89-1234-567-0");

        mockMvc.perform(post("/api/books")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookMap)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("도서 수정 실패 - 저자 누락")
    void update_fail_blankAuthor() throws Exception {
        Long bookId = createBookAndGetId("테스트 도서", "978-89-0000-001-0");

        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("title", "수정된 제목");
        updateMap.put("author", "");
        updateMap.put("publisher", "수정된 출판사");

        mockMvc.perform(put("/api/books/{id}", bookId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateMap)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }
}