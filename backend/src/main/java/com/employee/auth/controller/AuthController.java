package com.employee.auth.controller;

import com.employee.auth.dto.*;
import com.employee.auth.security.JwtTokenProvider;
import com.employee.auth.service.AuthService;
import com.employee.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 处理登录、登出、忘记密码、重置密码等请求
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应
     */
    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            LoginResponse response = authService.login(request);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.error("LOGIN_FAILED", e.getMessage());
        }
    }

    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    @PostMapping("/register")
    public ApiResponse<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        try {
            LoginResponse response = authService.register(request);
            return ApiResponse.success(response);
        } catch (RuntimeException e) {
            return ApiResponse.error("REGISTRATION_FAILED", e.getMessage());
        }
    }

    /**
     * 用户登出
     * @param authorization Authorization 头
     * @return 响应
     */
    @PostMapping("/logout")
    public ApiResponse<Void> logout(@RequestHeader("Authorization") String authorization) {
        String token = authorization.replace("Bearer ", "");
        authService.logout(token);
        return ApiResponse.success(null);
    }

    /**
     * 忘记密码
     * @param request 忘记密码请求
     * @return 响应
     */
    @PostMapping("/forgot-password")
    public ApiResponse<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        try {
            authService.forgotPassword(request);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.error("FORGOT_PASSWORD_FAILED", e.getMessage());
        }
    }

    /**
     * 重置密码
     * @param request 重置密码请求
     * @return 响应
     */
    @PostMapping("/reset-password")
    public ApiResponse<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        try {
            authService.resetPassword(request);
            return ApiResponse.success(null);
        } catch (RuntimeException e) {
            return ApiResponse.error("RESET_PASSWORD_FAILED", e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     * 注意：此接口保留用于向后兼容，建议使用 /api/users/me
     * @param authorization Authorization 头
     * @return 用户信息
     */
    @GetMapping("/me")
    public ApiResponse<UserInfoResponse> getCurrentUser(@RequestHeader("Authorization") String authorization) {
        try {
            String token = authorization.replace("Bearer ", "");
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            UserInfoResponse user = userService.getUserById(userId);
            return ApiResponse.success(user);
        } catch (RuntimeException e) {
            return ApiResponse.error("GET_USER_FAILED", e.getMessage());
        }
    }
}
