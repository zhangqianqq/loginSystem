package com.employee.auth.service.impl;

import com.employee.auth.dto.*;
import com.employee.auth.entity.PasswordReset;
import com.employee.auth.entity.User;
import com.employee.auth.repository.PasswordResetRepository;
import com.employee.auth.repository.UserRepository;
import com.employee.auth.security.JwtTokenProvider;
import com.employee.auth.service.AuthService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 认证服务实现类
 * 实现登录、注册、登出、忘记密码、重置密码等业务逻辑
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        // 查找用户
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("用户名或密码错误"));

        // 验证密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        // 检查账户是否激活
        if (!user.getIsActive()) {
            throw new RuntimeException("账户已被禁用");
        }

        // 生成 Token
        String token = jwtTokenProvider.generateToken(user);

        // 构建响应
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .build())
                .build();
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    @Override
    @Transactional
    public LoginResponse register(RegisterRequest request) {
        // 验证密码一致性
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("用户名已被占用");
        }

        // 检查邮箱是否已存在
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("邮箱已被注册");
        }

        // 创建新用户
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .isActive(true)
                .build();

        // 保存用户
        user = userRepository.save(user);

        log.info("New user registered successfully: {}", user.getUsername());

        // 生成 Token（注册后自动登录）
        String token = jwtTokenProvider.generateToken(user);

        // 构建响应
        return LoginResponse.builder()
                .token(token)
                .type("Bearer")
                .user(LoginResponse.UserInfo.builder()
                        .id(user.getId())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .fullName(user.getFullName())
                        .build())
                .build();
    }

    /**
     * 用户登出
     * @param token JWT Token
     */
    @Override
    public void logout(String token) {
        // TODO: 实现 Token 黑名单机制（使用 Redis）
        log.info("User logged out");
    }

    /**
     * 忘记密码 - 发送重置邮件
     * @param request 忘记密码请求
     */
    @Override
    @Transactional
    public void forgotPassword(ForgotPasswordRequest request) {
        // 查找用户
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("该邮箱未注册"));

        // 生成重置 Token
        String resetToken = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusHours(1); // 1小时后过期

        // 保存重置记录
        PasswordReset passwordReset = PasswordReset.builder()
                .userId(user.getId())
                .token(resetToken)
                .expiresAt(expiresAt)
                .build();
        passwordResetRepository.save(passwordReset);

        // 发送邮件（TODO: 实现邮件发送功能）
        log.info("Password reset email sent to: {}", user.getEmail());
        log.info("Reset token: {}", resetToken);
    }

    /**
     * 重置密码
     * @param request 重置密码请求
     */
    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        // 查找重置记录
        PasswordReset passwordReset = passwordResetRepository.findByToken(request.getToken())
                .orElseThrow(() -> new RuntimeException("重置链接无效"));

        // 检查是否过期
        if (passwordReset.isExpired()) {
            throw new RuntimeException("重置链接已过期");
        }

        // 查找用户
        User user = userRepository.findById(passwordReset.getUserId())
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 更新密码
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        // 删除重置记录
        passwordResetRepository.delete(passwordReset);

        log.info("Password reset successfully for user: {}", user.getUsername());
    }

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    @Override
    public Object getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        return LoginResponse.UserInfo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .build();
    }
}
