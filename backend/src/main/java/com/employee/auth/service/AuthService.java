package com.employee.auth.service;

import com.employee.auth.dto.ForgotPasswordRequest;
import com.employee.auth.dto.LoginRequest;
import com.employee.auth.dto.LoginResponse;
import com.employee.auth.dto.RegisterRequest;
import com.employee.auth.dto.ResetPasswordRequest;

/**
 * 认证服务接口
 * 定义登录、登出、忘记密码、重置密码等业务方法
 */
public interface AuthService {

    /**
     * 用户登录
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注册
     * @param request 注册请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse register(RegisterRequest request);

    /**
     * 用户登出
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 忘记密码 - 发送重置邮件
     * @param request 忘记密码请求
     */
    void forgotPassword(ForgotPasswordRequest request);

    /**
     * 重置密码
     * @param request 重置密码请求
     */
    void resetPassword(ResetPasswordRequest request);

    /**
     * 获取当前用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Object getCurrentUser(Long userId);
}
