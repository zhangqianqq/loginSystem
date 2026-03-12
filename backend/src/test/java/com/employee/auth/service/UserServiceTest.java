package com.employee.auth.service;

import com.employee.auth.dto.ChangePasswordRequest;
import com.employee.auth.dto.UpdateUserRequest;
import com.employee.auth.dto.UserInfoResponse;
import com.employee.auth.entity.User;
import com.employee.auth.exception.InvalidPasswordException;
import com.employee.auth.exception.ResourceNotFoundException;
import com.employee.auth.repository.UserRepository;
import com.employee.auth.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 用户服务测试类
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("用户服务测试")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .password("encodedPassword")
                .fullName("测试用户")
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("根据ID获取用户信息 - 成功")
    void getUserById_Success() {
        // Given
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));

        // When
        UserInfoResponse response = userService.getUserById(userId);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        assertThat(response.getEmail()).isEqualTo("test@example.com");
        assertThat(response.getFullName()).isEqualTo("测试用户");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("根据ID获取用户信息 - 用户不存在")
    void getUserById_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("用户");

        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("更新用户全名 - 成功")
    void updateUser_UpdateFullName_Success() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .fullName("新名称")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserInfoResponse response = userService.updateUser(userId, request);

        // Then
        assertThat(response.getFullName()).isEqualTo("新名称");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("更新用户邮箱 - 成功")
    void updateUser_UpdateEmail_Success() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("newemail@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("newemail@example.com", userId)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        UserInfoResponse response = userService.updateUser(userId, request);

        // Then
        assertThat(response.getEmail()).isEqualTo("newemail@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("更新用户邮箱 - 邮箱已被使用")
    void updateUser_UpdateEmail_AlreadyExists() {
        // Given
        Long userId = 1L;
        UpdateUserRequest request = UpdateUserRequest.builder()
                .email("existing@example.com")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(userRepository.existsByEmailAndIdNot("existing@example.com", userId)).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.updateUser(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("该邮箱已被其他用户使用");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("修改密码 - 成功")
    void changePassword_Success() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);
        when(passwordEncoder.matches("newPassword123", "encodedPassword")).thenReturn(false);
        when(passwordEncoder.encode("newPassword123")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // When
        userService.changePassword(userId, request);

        // Then
        verify(passwordEncoder).encode("newPassword123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("修改密码 - 当前密码不正确")
    void changePassword_CurrentPasswordIncorrect() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("wrongPassword")
                .newPassword("newPassword123")
                .confirmPassword("newPassword123")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongPassword", "encodedPassword")).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(InvalidPasswordException.class);

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("修改密码 - 两次密码不一致")
    void changePassword_PasswordsNotMatch() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("newPassword123")
                .confirmPassword("differentPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("两次输入的新密码不一致");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("修改密码 - 新密码与当前密码相同")
    void changePassword_NewPasswordSameAsCurrent() {
        // Given
        Long userId = 1L;
        ChangePasswordRequest request = ChangePasswordRequest.builder()
                .currentPassword("oldPassword")
                .newPassword("oldPassword")
                .confirmPassword("oldPassword")
                .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("oldPassword", "encodedPassword")).thenReturn(true);

        // When & Then
        assertThatThrownBy(() -> userService.changePassword(userId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("新密码不能与当前密码相同");

        verify(passwordEncoder, never()).encode(anyString());
        verify(userRepository, never()).save(any(User.class));
    }
}
