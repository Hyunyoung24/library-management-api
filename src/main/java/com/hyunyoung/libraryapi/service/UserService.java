package com.hyunyoung.libraryapi.service;

import com.hyunyoung.libraryapi.dto.request.AdminUserUpdateRequest;
import com.hyunyoung.libraryapi.dto.request.UserUpdateRequest;
import com.hyunyoung.libraryapi.entity.User;
import com.hyunyoung.libraryapi.enums.Authority;
import com.hyunyoung.libraryapi.exception.DuplicateResourceException;
import com.hyunyoung.libraryapi.exception.ResourceNotFoundException;
import com.hyunyoung.libraryapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("사용자를 찾을 수 없습니다."));
    }

    @Transactional
    public User updateMyInfo(String username, UserUpdateRequest request) {
        User user = findByUsername(username);

        if (request.getEmail() != null) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new DuplicateResourceException("이미 존재하는 이메일입니다.");
            }
            user.updateInfo(request.getEmail());
        }
        if (request.getPassword() != null) {
            user.updatePassword(passwordEncoder.encode(request.getPassword()));
        }

        return user;
    }

    @Transactional
    public User updateAuthority(Long id, AdminUserUpdateRequest request) {
        User user = findById(id);
        Authority authority = Authority.valueOf(request.getAuthority());
        user.updateAuthority(authority);
        return user;
    }

    @Transactional
    public void delete(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}