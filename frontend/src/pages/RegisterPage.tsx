/**
 * 注册页面组件
 */

import { useState } from 'react';
import { authService } from '../services/auth';
import type { ValidationError } from '../types/auth';
import './LoginPage.css';

interface RegisterPageProps {
  onSwitchToLogin?: () => void;
}

export default function RegisterPage({ onSwitchToLogin }: RegisterPageProps) {
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState<ValidationError[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  /**
   * 验证注册表单
   */
  const validateForm = (): ValidationError[] => {
    const validationErrors: ValidationError[] = [];

    // 用户名验证
    if (!username) {
      validationErrors.push({ field: 'username', message: '请输入用户名' });
    } else if (username.length < 3) {
      validationErrors.push({ field: 'username', message: '用户名至少 3 个字符' });
    } else if (!/^[a-zA-Z0-9_]+$/.test(username)) {
      validationErrors.push({ field: 'username', message: '用户名只能包含字母、数字和下划线' });
    }

    // 邮箱验证
    if (!email) {
      validationErrors.push({ field: 'email', message: '请输入邮箱' });
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email)) {
      validationErrors.push({ field: 'email', message: '请输入有效的邮箱地址' });
    }

    // 密码验证
    if (!password) {
      validationErrors.push({ field: 'password', message: '请输入密码' });
    } else if (password.length < 8) {
      validationErrors.push({ field: 'password', message: '密码至少 8 个字符' });
    } else if (!/[a-zA-Z]/.test(password) || !/[0-9]/.test(password)) {
      validationErrors.push({ field: 'password', message: '密码必须包含字母和数字' });
    }

    // 确认密码验证
    if (!confirmPassword) {
      validationErrors.push({ field: 'confirmPassword', message: '请确认密码' });
    } else if (password !== confirmPassword) {
      validationErrors.push({ field: 'confirmPassword', message: '两次输入的密码不一致' });
    }

    return validationErrors;
  };

  /**
   * 处理表单提交
   */
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    // 清空之前的消息
    setSuccessMessage('');
    setErrors([]);

    // 验证表单
    const validationErrors = validateForm();
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      return;
    }

    setIsLoading(true);

    try {
      await authService.register({ username, email, password, confirmPassword });
      setSuccessMessage('注册成功！正在跳转...');

      // 延迟跳转到仪表板
      setTimeout(() => {
        window.location.href = '/dashboard.html';
      }, 1000);
    } catch (error) {
      setErrors([{ field: 'general', message: (error as Error).message }]);
    } finally {
      setIsLoading(false);
    }
  };

  /**
   * 获取字段错误信息
   */
  const getFieldError = (field: string): string | undefined => {
    return errors.find((e) => e.field === field)?.message;
  };

  return (
    <div className="login-container">
      <div className="login-card">
        <div className="login-header">
          <h1>用户注册</h1>
          <p>创建您的账户</p>
        </div>

        <form className="login-form" onSubmit={handleSubmit}>
          {/* 成功消息 */}
          {successMessage && (
            <div className="alert alert-success">{successMessage}</div>
          )}

          {/* 通用错误消息 */}
          {getFieldError('general') && (
            <div className="alert alert-error">{getFieldError('general')}</div>
          )}

          {/* 用户名输入框 */}
          <div className="form-group">
            <label htmlFor="username">用户名</label>
            <input
              id="username"
              type="text"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              placeholder="3-20个字符，字母、数字或下划线"
              className={getFieldError('username') ? 'input-error' : ''}
            />
            {getFieldError('username') && (
              <span className="error-text">{getFieldError('username')}</span>
            )}
          </div>

          {/* 邮箱输入框 */}
          <div className="form-group">
            <label htmlFor="email">邮箱</label>
            <input
              id="email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="请输入邮箱地址"
              className={getFieldError('email') ? 'input-error' : ''}
            />
            {getFieldError('email') && (
              <span className="error-text">{getFieldError('email')}</span>
            )}
          </div>

          {/* 密码输入框 */}
          <div className="form-group">
            <label htmlFor="password">密码</label>
            <input
              id="password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="至少8个字符，包含字母和数字"
              className={getFieldError('password') ? 'input-error' : ''}
            />
            {getFieldError('password') && (
              <span className="error-text">{getFieldError('password')}</span>
            )}
          </div>

          {/* 确认密码输入框 */}
          <div className="form-group">
            <label htmlFor="confirmPassword">确认密码</label>
            <input
              id="confirmPassword"
              type="password"
              value={confirmPassword}
              onChange={(e) => setConfirmPassword(e.target.value)}
              placeholder="请再次输入密码"
              className={getFieldError('confirmPassword') ? 'input-error' : ''}
            />
            {getFieldError('confirmPassword') && (
              <span className="error-text">{getFieldError('confirmPassword')}</span>
            )}
          </div>

          {/* 注册按钮 */}
          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? '注册中...' : '注册'}
          </button>

          {/* 返回登录链接 */}
          <div className="form-footer">
            <div className="form-footer-links">
              <span>已有账户？</span>
              {onSwitchToLogin ? (
                <a href="#" onClick={(e) => { e.preventDefault(); onSwitchToLogin(); }}>
                  立即登录
                </a>
              ) : (
                <a href="/">立即登录</a>
              )}
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
