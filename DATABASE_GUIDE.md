# 数据库初始化指南

## 📍 找到的 MySQL 安装位置

`C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe`

---

## 🚀 初始化数据库（选择一种方式）

### 方式1：使用批处理脚本（推荐）

双击运行：`k:\Cursor\loginSystem\init-database.bat`

在提示时输入 MySQL root 密码。

### 方式2：使用 PowerShell 脚本

1. 右键点击 `init-database.ps1`
2. 选择 **使用 PowerShell 运行**
3. 输入 MySQL root 密码

### 方式3：使用命令行

打开 **CMD** 或 **PowerShell**：

```bash
cd k:\Cursor\loginSystem\database\migrations
"C:\Program Files\MySQL\MySQL Server 8.4\bin\mysql.exe" -u root -p < V1__init_schema.sql
```

### 方式4：使用 MySQL Workbench

1. 打开 MySQL Workbench
2. 连接到本地 MySQL 服务器
3. 点击 **File** → **Open SQL Script**
4. 选择 `k:\Cursor\loginSystem\database\igrations\V1__init_schema.sql`
5. 点击执行按钮（闪电图标）

### 方式5：复制粘贴 SQL（最简单）

1. 打开 MySQL Workbench 或命令行
2. 连接到数据库
3. 复制下面的 SQL 内容并执行

```sql
-- ========================================
-- 员工登录系统 - 数据库初始化脚本
-- ========================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS employee_system
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE employee_system;

-- ========================================
-- 用户表
-- ========================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '用户ID（主键）',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '加密密码（BCrypt）',
    email VARCHAR(100) NOT NULL UNIQUE COMMENT '邮箱',
    full_name VARCHAR(100) COMMENT '全名',
    is_active TINYINT(1) NOT NULL DEFAULT 1 COMMENT '是否激活（1:激活 0:禁用）',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ========================================
-- 密码重置表
-- ========================================
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

-- ========================================
-- 初始化管理员账户
-- 密码: admin123（BCrypt 加密后）
-- ========================================
INSERT INTO users (username, password, email, full_name, is_active)
VALUES (
    'admin',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'admin@example.com',
    '系统管理员',
    1
) ON DUPLICATE KEY UPDATE username = username;

-- ========================================
-- 验证数据
-- ========================================
SELECT 'Database initialized successfully!' AS status;
SELECT COUNT(*) AS user_count FROM users;
SELECT * FROM users;
```

---

## ✅ 验证数据库是否初始化成功

执行以下命令验证：

```sql
USE employee_system;
SELECT * FROM users;
```

应该看到一条记录：
- id: 1
- username: admin
- email: admin@example.com
- full_name: 系统管理员

---

## 🔧 配置后端数据库连接

修改文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    username: root
    password: 你的MySQL密码  # 修改这里
```

---

## 📋 数据库连接信息

| 配置项 | 值 |
|--------|-----|
| 主机 | localhost |
| 端口 | 3306 |
| 数据库 | employee_system |
| 用户名 | root |
| 密码 | 你的密码 |

---

## 🎯 下一步

数据库初始化完成后：

1. ✅ 配置后端 application.yml 中的数据库密码
2. ✅ 启动后端服务（使用 IDEA）
3. ✅ 测试登录功能

---

*更新时间：2026-03-10*
