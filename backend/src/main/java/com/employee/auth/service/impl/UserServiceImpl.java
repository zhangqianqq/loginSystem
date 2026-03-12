package com.employee.auth.service.impl;

import com.employee.auth.dto.ChangePasswordRequest;
import com.employee.auth.dto.UpdateUserRequest;
import com.employee.auth.dto.UserInfoResponse;
import com.employee.auth.entity.User;
import com.employee.auth.exception.InvalidPasswordException;
import com.employee.auth.exception.ResourceNotFoundException;
import com.employee.auth.repository.UserRepository;
import com.employee.auth.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 用户服务实现类
 * 实现用户信息查询、更新、密码修改等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息响应
     * @throws ResourceNotFoundException 当用户不存在时抛出
     */
    @Override
    public UserInfoResponse getUserById(Long userId) {
        User user = findUserById(userId);
        return mapToUserInfoResponse(user);
    }

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     * @throws ResourceNotFoundException 当用户不存在时抛出
     */
    @Override
    @Transactional
    public UserInfoResponse updateUser(Long userId, UpdateUserRequest request) {
        User user = findUserById(userId);

        // 更新全名（如果提供）
        if (request.getFullName() != null) {
            user.setFullName(request.getFullName());
        }

        // 更新邮箱（如果提供）
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            if (userRepository.existsByEmailAndIdNot(request.getEmail(), userId)) {
                throw new IllegalArgumentException("该邮箱已被其他用户使用");
            }
            user.setEmail(request.getEmail());
        }

        // 保存更新
        User updatedUser = userRepository.save(user);

        log.info("User info updated: userId={}, username={}", userId, updatedUser.getUsername());

        return mapToUserInfoResponse(updatedUser);
    }

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param request 修改密码请求
     * @throws ResourceNotFoundException 当用户不存在时抛出
     * @throws InvalidPasswordException 当当前密码不正确时抛出
     */
    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = findUserById(userId);

        // 验证当前密码
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new InvalidPasswordException("当前密码不正确");
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的新密码不一致");
        }

        // 新密码不能与当前密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new IllegalArgumentException("新密码不能与当前密码相同");
        }

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        log.info("Password changed successfully: userId={}, username={}", userId, user.getUsername());
    }

    /**
     * 根据ID查找用户
     * @param userId 用户ID
     * @return 用户实体
     * @throws ResourceNotFoundException 当用户不存在时抛出
     */
    private User findUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("用户", userId));
    }

    /**
     * 将 User 实体转换为 UserInfoResponse DTO
     * @param user 用户实体
     * @return 用户信息响应
     */
    private UserInfoResponse mapToUserInfoResponse(User user) {
        return UserInfoResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .isActive(user.getIsActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
