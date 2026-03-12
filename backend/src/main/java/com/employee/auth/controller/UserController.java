package com.employee.auth.controller;

import com.employee.auth.dto.ChangePasswordRequest;
import com.employee.auth.dto.UpdateUserRequest;
import com.employee.auth.dto.UserInfoResponse;
import com.employee.auth.security.JwtTokenProvider;
import com.employee.auth.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 * 处理用户信息查询、更新、密码修改等请求
 */
@Slf4j
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 获取当前用户信息
     * 从 JWT Token 中解析用户ID并返回用户详细信息
     * @param authorization Authorization 头（包含 JWT Token）
     * @return 用户信息
     */
    @GetMapping("/me")
    public UserInfoResponse getCurrentUser(@RequestHeader("Authorization") String authorization) {
        String token = extractToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        log.info("Getting user info for userId: {}", userId);
        return userService.getUserById(userId);
    }

    /**
     * 更新当前用户信息
     * @param authorization Authorization 头（包含 JWT Token）
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    @PutMapping("/me")
    public UserInfoResponse updateCurrentUser(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody UpdateUserRequest request) {
        String token = extractToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        log.info("Updating user info for userId: {}", userId);
        return userService.updateUser(userId, request);
    }

    /**
     * 修改当前用户密码
     * @param authorization Authorization 头（包含 JWT Token）
     * @param request 修改密码请求
     * @return 操作结果
     */
    @PutMapping("/me/password")
    public String changePassword(
            @RequestHeader("Authorization") String authorization,
            @Valid @RequestBody ChangePasswordRequest request) {
        String token = extractToken(authorization);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);
        log.info("Changing password for userId: {}", userId);
        userService.changePassword(userId, request);
        return "密码修改成功";
    }

    /**
     * 从 Authorization 头中提取 Token
     * @param authorization Authorization 头
     * @return JWT Token
     */
    private String extractToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new IllegalArgumentException("无效的 Authorization 头");
        }
        return authorization.substring(7);
    }
}
