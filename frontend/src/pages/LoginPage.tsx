/**
 * 登录页面组件
 */

import { useState } from 'react';
import { authService } from '../services/auth';
import { validateLoginForm } from '../utils/validation';
import type { ValidationError } from '../types/auth';
import './LoginPage.css';

interface LoginPageProps {
  onSwitchToRegister?: () => void;
}

export default function LoginPage({ onSwitchToRegister }: LoginPageProps) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errors, setErrors] = useState<ValidationError[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [successMessage, setSuccessMessage] = useState('');

  /**
   * 处理表单提交
   */
  const handleSubmit = async (event: React.FormEvent) => {
    event.preventDefault();

    // 清空之前的消息
    setSuccessMessage('');
    setErrors([]);

    // 验证表单
    const validationErrors = validateLoginForm(username, password);
    if (validationErrors.length > 0) {
      setErrors(validationErrors);
      return;
    }

    setIsLoading(true);

    try {
      await authService.login({ username, password });
      setSuccessMessage('登录成功！正在跳转...');

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
          <h1>员工登录系统</h1>
          <p>Employee Login System</p>
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
              placeholder="请输入用户名"
              className={getFieldError('username') ? 'input-error' : ''}
            />
            {getFieldError('username') && (
              <span className="error-text">{getFieldError('username')}</span>
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
              placeholder="请输入密码"
              className={getFieldError('password') ? 'input-error' : ''}
            />
            {getFieldError('password') && (
              <span className="error-text">{getFieldError('password')}</span>
            )}
          </div>

          {/* 登录按钮 */}
          <button type="submit" className="btn btn-primary" disabled={isLoading}>
            {isLoading ? '登录中...' : '登录'}
          </button>

          {/* 忘记密码和注册链接 */}
          <div className="form-footer">
            <div className="form-footer-links">
              <a href="/forgot-password.html">忘记密码？</a>
              {onSwitchToRegister && (
                <>
                  <span className="divider">|</span>
                  <a href="#" onClick={(e) => { e.preventDefault(); onSwitchToRegister(); }}>
                    还没有账户？立即注册
                  </a>
                </>
              )}
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
