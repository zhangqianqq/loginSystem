/**
 * 认证相关 API 服务
 * 处理登录、登出、忘记密码、重置密码等 API 请求
 */

import type {
  ApiResponse,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  RegisterRequest,
  ResetPasswordRequest,
  UserInfo,
} from '../types/auth';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080/api';

/**
 * 认证服务类
 */
class AuthService {
  private baseURL = `${API_BASE_URL}/auth`;

  /**
   * 用户登录
   * @param loginRequest 登录请求参数
   * @returns 登录响应
   */
  async login(loginRequest: LoginRequest): Promise<LoginResponse> {
    const response = await fetch(`${this.baseURL}/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(loginRequest),
    });

    const result: ApiResponse<LoginResponse> = await response.json();

    if (!result.success || !result.data) {
      throw new Error(result.error?.message || '登录失败');
    }

    // 保存 Token 到 localStorage
    localStorage.setItem('token', result.data.token);
    localStorage.setItem('user', JSON.stringify(result.data.user));

    return result.data;
  }

  /**
   * 用户注册
   * @param registerRequest 注册请求参数
   * @returns 登录响应（包含 Token 和用户信息）
   */
  async register(registerRequest: RegisterRequest): Promise<LoginResponse> {
    const response = await fetch(`${this.baseURL}/register`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(registerRequest),
    });

    const result: ApiResponse<LoginResponse> = await response.json();

    if (!result.success || !result.data) {
      throw new Error(result.error?.message || '注册失败');
    }

    // 保存 Token 到 localStorage（注册后自动登录）
    localStorage.setItem('token', result.data.token);
    localStorage.setItem('user', JSON.stringify(result.data.user));

    return result.data;
  }

  /**
   * 用户登出
   */
  async logout(): Promise<void> {
    const token = localStorage.getItem('token');
    if (token) {
      await fetch(`${this.baseURL}/logout`, {
        method: 'POST',
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
    }

    // 清除本地存储
    localStorage.removeItem('token');
    localStorage.removeItem('user');
  }

  /**
   * 忘记密码
   * @param request 忘记密码请求参数
   */
  async forgotPassword(request: ForgotPasswordRequest): Promise<void> {
    const response = await fetch(`${this.baseURL}/forgot-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    const result: ApiResponse<void> = await response.json();

    if (!result.success) {
      throw new Error(result.error?.message || '发送失败');
    }
  }

  /**
   * 重置密码
   * @param request 重置密码请求参数
   */
  async resetPassword(request: ResetPasswordRequest): Promise<void> {
    const response = await fetch(`${this.baseURL}/reset-password`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(request),
    });

    const result: ApiResponse<void> = await response.json();

    if (!result.success) {
      throw new Error(result.error?.message || '重置失败');
    }
  }

  /**
   * 获取当前用户信息
   * @returns 用户信息
   */
  async getCurrentUser(): Promise<UserInfo> {
    const token = localStorage.getItem('token');
    if (!token) {
      throw new Error('未登录');
    }

    const response = await fetch(`${this.baseURL}/me`, {
      method: 'GET',
      headers: {
        Authorization: `Bearer ${token}`,
      },
    });

    const result: ApiResponse<UserInfo> = await response.json();

    if (!result.success || !result.data) {
      throw new Error(result.error?.message || '获取用户信息失败');
    }

    return result.data;
  }

  /**
   * 获取本地存储的 Token
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * 获取本地存储的用户信息
   */
  getLocalUser(): UserInfo | null {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
  }

  /**
   * 检查是否已登录
   */
  isAuthenticated(): boolean {
    return !!this.getToken();
  }
}

// 导出单例实例
export const authService = new AuthService();
