/**
 * API 响应类型定义
 */

/**
 * 统一 API 响应格式
 */
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
  };
}

/**
 * 登录请求
 */
export interface LoginRequest {
  username: string;
  password: string;
}

/**
 * 登录响应
 */
export interface LoginResponse {
  token: string;
  type: string;
  user: UserInfo;
}

/**
 * 用户信息
 */
export interface UserInfo {
  id: number;
  username: string;
  email: string;
  fullName?: string;
}

/**
 * 忘记密码请求
 */
export interface ForgotPasswordRequest {
  email: string;
}

/**
 * 重置密码请求
 */
export interface ResetPasswordRequest {
  token: string;
  newPassword: string;
}

/**
 * 注册请求
 */
export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  confirmPassword: string;
}

/**
 * 表单验证错误
 */
export interface ValidationError {
  field: string;
  message: string;
}
