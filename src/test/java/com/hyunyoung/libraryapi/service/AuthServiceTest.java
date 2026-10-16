package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.config.JwtTokenProvider;
import com.hyunyoung.libraryapi.dto.request.LoginRequest;
import com.hyunyoung.libraryapi.dto.request.RegisterRequest;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.exception.DuplicateResourceException;
import com.hyunyoung.libraryapi.exception.InvalidRequestException;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    private RegisterRequest createRegisterRequest(String username, String email, String password) {
        RegisterRequest request = new RegisterRequest();
        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "email", email);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    private LoginRequest createLoginRequest(String username, String password) {
        LoginRequest request = new LoginRequest();
        ReflectionTestUtils.setField(request, "username", username);
        ReflectionTestUtils.setField(request, "password", password);
        return request;
    }

    @Test
    @DisplayName("회원가입 - 성공")
    void register_success() {
        // given
        RegisterRequest request = createRegisterRequest("testuser", "test@test.com", "password123");
        User user = User.create("testuser", "test@test.com", "encodedPassword");

        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(false);
        given(passwordEncoder.encode("password123")).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(user);

        // when
        User result = authService.register(request);

        // then
        assertThat(result.getUsername()).isEqualTo("testuser");
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("회원가입 - 중복 사용자명")
    void register_duplicateUsername() {
        // given
        RegisterRequest request = createRegisterRequest("testuser", "test@test.com", "password123");
        given(userRepository.existsByUsername("testuser")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 존재하는 사용자명입니다.");
    }

    @Test
    @DisplayName("회원가입 - 중복 이메일")
    void register_duplicateEmail() {
        // given
        RegisterRequest request = createRegisterRequest("testuser", "test@test.com", "password123");
        given(userRepository.existsByUsername("testuser")).willReturn(false);
        given(userRepository.existsByEmail("test@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("로그인 - 성공")
    void login_success() {
        // given
        LoginRequest request = createLoginRequest("testuser", "password123");
        User user = User.create("testuser", "test@test.com", "encodedPassword");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("password123", "encodedPassword")).willReturn(true);
        given(jwtTokenProvider.createToken("testuser", "USER")).willReturn("jwt-token");

        // when
        String token = authService.login(request);

        // then
        assertThat(token).isEqualTo("jwt-token");
    }

    @Test
    @DisplayName("로그인 - 존재하지 않는 사용자")
    void login_userNotFound() {
        // given
        LoginRequest request = createLoginRequest("nobody", "password123");
        given(userRepository.findByUsername("nobody")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("로그인 - 비밀번호 불일치")
    void login_wrongPassword() {
        // given
        LoginRequest request = createLoginRequest("testuser", "wrongpassword");
        User user = User.create("testuser", "test@test.com", "encodedPassword");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.matches("wrongpassword", "encodedPassword")).willReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessage("비밀번호가 일치하지 않습니다.");
    }
}