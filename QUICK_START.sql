-- ========================================
-- 员工登录系统 - 数据库初始化 SQL
-- 直接复制此内容到 MySQL Workbench 中执行
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS employee_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE employee_system;

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID（主键）',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    full_name VARCHAR(100) COMMENT '全名',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 创建密码重置表
CREATE TABLE IF NOT EXISTS password_resets (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '重置ID（主键）',
    user_id BIGINT NOT NULL COMMENT '用户ID（外键）',
    token VARCHAR(255) NOT NULL UNIQUE COMMENT '重置Token',
    expires_at DATETIME NOT NULL COMMENT '过期时间',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_token (token),
    INDEX idx_user_id (user_id),
    INDEX idx_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='密码重置表';

-- 插入管理员账户（密码：admin123）
INSERT INTO users (username, password, email, full_name, is_active)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@example.com',
    '系统管理员',
    1
) ON DUPLICATE KEY UPDATE username = username;

-- 验证初始化
SELECT '✅ 数据库初始化完成！' AS status;
SELECT '用户名: admin, 密码: admin123' AS info;
SELECT * FROM users;
