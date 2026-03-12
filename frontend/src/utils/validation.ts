/**
 * 表单验证工具函数
 */

import type { ValidationError } from '../types/auth';

/**
 * 验证用户名
 * @param username 用户名
 * @returns 验证错误信息，无错误返回 null
 */
export function validateUsername(username: string): ValidationError | null {
  if (!username) {
    return { field: 'username', message: '用户名不能为空' };
  }
  if (username.length < 3) {
    return { field: 'username', message: '用户名至少需要3个字符' };
  }
  if (username.length > 20) {
    return { field: 'username', message: '用户名最多20个字符' };
  }
  return null;
}

/**
 * 验证密码
 * @param password 密码
 * @returns 验证错误信息，无错误返回 null
 */
export function validatePassword(password: string): ValidationError | null {
  if (!password) {
    return { field: 'password', message: '密码不能为空' };
  }
  if (password.length < 8) {
    return { field: 'password', message: '密码至少需要8个字符' };
  }
  if (!/(?=.*[a-zA-Z])(?=.*\d)/.test(password)) {
    return { field: 'password', message: '密码必须包含字母和数字' };
  }
  return null;
}

/**
 * 验证邮箱
 * @param email 邮箱地址
 * @returns 验证错误信息，无错误返回 null
 */
export function validateEmail(email: string): ValidationError | null {
  if (!email) {
    return { field: 'email', message: '邮箱不能为空' };
  }
  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(email)) {
    return { field: 'email', message: '邮箱格式不正确' };
  }
  return null;
}

/**
 * 验证登录表单
 * @param username 用户名
 * @param password 密码
 * @returns 验证错误信息数组
 */
export function validateLoginForm(
  username: string,
  password: string
): ValidationError[] {
  const errors: ValidationError[] = [];

  const usernameError = validateUsername(username);
  if (usernameError) errors.push(usernameError);

  const passwordError = validatePassword(password);
  if (passwordError) errors.push(passwordError);

  return errors;
}
