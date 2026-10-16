package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.dto.request.AdminUserUpdateRequest;
import com.hyunyoung.libraryapi.dto.request.UserUpdateRequest;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.enums.Authority;
import com.hyunyoung.libraryapi.exception.DuplicateResourceException;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("사용자 목록 조회")
    void findAll() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        User user = User.create("testuser", "test@test.com", "password");
        Page<User> page = new PageImpl<>(List.of(user));
        given(userRepository.findAll(pageable)).willReturn(page);

        // when
        Page<User> result = userService.findAll(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("사용자 조회 - ID로 성공")
    void findById_success() {
        // given
        User user = User.create("testuser", "test@test.com", "password");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User result = userService.findById(1L);

        // then
        assertThat(result.getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 ID")
    void findById_notFound() {
        // given
        given(userRepository.findById(1L)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("사용자 조회 - username으로 성공")
    void findByUsername_success() {
        // given
        User user = User.create("testuser", "test@test.com", "password");
        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));

        // when
        User result = userService.findByUsername("testuser");

        // then
        assertThat(result.getEmail()).isEqualTo("test@test.com");
    }

    @Test
    @DisplayName("사용자 조회 - 존재하지 않는 username")
    void findByUsername_notFound() {
        // given
        given(userRepository.findByUsername("nobody")).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> userService.findByUsername("nobody"))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");
    }

    @Test
    @DisplayName("내 정보 수정 - 이메일 변경")
    void updateMyInfo_email() {
        // given
        User user = User.create("testuser", "old@test.com", "password");
        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "email", "new@test.com");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("new@test.com")).willReturn(false);

        // when
        User result = userService.updateMyInfo("testuser", request);

        // then
        assertThat(result.getEmail()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("내 정보 수정 - 비밀번호 변경")
    void updateMyInfo_password() {
        // given
        User user = User.create("testuser", "test@test.com", "oldpassword");
        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "password", "newpassword");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(passwordEncoder.encode("newpassword")).willReturn("encodedPassword");

        // when
        User result = userService.updateMyInfo("testuser", request);

        // then
        assertThat(result.getPassword()).isEqualTo("encodedPassword");
    }

    @Test
    @DisplayName("내 정보 수정 - 중복 이메일")
    void updateMyInfo_duplicateEmail() {
        // given
        User user = User.create("testuser", "old@test.com", "password");
        UserUpdateRequest request = new UserUpdateRequest();
        ReflectionTestUtils.setField(request, "email", "duplicate@test.com");

        given(userRepository.findByUsername("testuser")).willReturn(Optional.of(user));
        given(userRepository.existsByEmail("duplicate@test.com")).willReturn(true);

        // when & then
        assertThatThrownBy(() -> userService.updateMyInfo("testuser", request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("이미 존재하는 이메일입니다.");
    }

    @Test
    @DisplayName("권한 변경")
    void updateAuthority() {
        // given
        User user = User.create("testuser", "test@test.com", "password");
        AdminUserUpdateRequest request = new AdminUserUpdateRequest();
        ReflectionTestUtils.setField(request, "authority", "ADMIN");

        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        User result = userService.updateAuthority(1L, request);

        // then
        assertThat(result.getAuthority()).isEqualTo(Authority.ADMIN);
    }

    @Test
    @DisplayName("사용자 삭제")
    void delete() {
        // given
        User user = User.create("testuser", "test@test.com", "password");
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        // when
        userService.delete(1L);

        // then
        verify(userRepository).delete(user);
    }
}