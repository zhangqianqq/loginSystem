# 员工登录系统开发计划

## 项目概述

开发一个完整的员工登录系统，包含前端登录页面、后端 API 和数据库连接。

**项目目录**: `k:/Cursor/loginSystem`

---

## 技术栈

### 前端
| 技术 | 版本 | 用途 |
|------|------|------|
| **React** | 18.x | UI 框架 |
| **TypeScript** | 5.x | 类型安全 |
| **Vite** | 5.x | 构建工具 |
| **React Router** | 6.x | 路由管理 |
| **Axios** | 1.x | HTTP 请求 |
| **CSS Modules** | - | 样式隔离 |

### 后端
| 技术 | 版本 | 用途 |
|------|------|------|
| **Java** | 17+ | 编程语言 |
| **Spring Boot** | 3.2.x | Web 框架 |
| **Spring Security** | 6.2.x | 安全认证 |
| **Spring Data JPA** | 3.2.x | ORM 框架 |
| **MySQL Connector** | 8.3.x | 数据库驱动 |
| **JJWT** | 0.12.x | JWT 认证 |
| **Java Mail** | 1.6.x | 邮件发送 |
| **Lombok** | 1.18.x | 简化代码 |
| **Maven** | 3.9.x | 构建工具 |

### 数据库
| 技术 | 版本 | 用途 |
|------|------|------|
| **MySQL** | 8.x | 关系型数据库 |

---

## 功能模块

### 1. 登录功能
- [ ] 用户名/密码输入表单
- [ ] 表单验证（非空、格式检查）
- [ ] 登录请求发送
- [ ] 错误提示（用户不存在、密码错误）
- [ ] 登录成功跳转

### 2. 用户验证
- [ ] 数据库用户查询
- [ ] 密码加密验证（BCrypt）
- [ ] JWT Token 生成与验证
- [ ] Token 存储与管理

### 3. 忘记密码
- [ ] 忘记密码入口
- [ ] 邮箱输入验证
- [ ] 发送重置邮件
- [ ] 密码重置链接（含有效期）
- [ ] 新密码设置页面

### 4. 用户状态管理
- [ ] 登录状态持久化
- [ ] Token 自动刷新
- [ ] 退出登录功能

---

## 数据库设计

### 用户表 (users)

| 字段名 | 类型 | 长度 | 是否可为空 | 说明 |
|--------|------|------|------------|------|
| id | INT | - | NOT NULL | 主键，自增 |
| username | VARCHAR | 50 | NOT NULL | 用户名，唯一 |
| password | VARCHAR | 255 | NOT NULL | 加密密码 |
| email | VARCHAR | 100 | NOT NULL | 邮箱，唯一 |
| full_name | VARCHAR | 100 | NULL | 全名 |
| is_active | TINYINT | 1 | NOT NULL | 是否激活 |
| created_at | DATETIME | - | NOT NULL | 创建时间 |
| updated_at | DATETIME | - | NOT NULL | 更新时间 |

### 密码重置表 (password_resets)

| 字段名 | 类型 | 长度 | 是否可为空 | 说明 |
|--------|------|------|------------|------|
| id | INT | - | NOT NULL | 主键，自增 |
| user_id | INT | - | NOT NULL | 用户 ID（外键） |
| token | VARCHAR | 255 | NOT NULL | 重置 Token |
| expires_at | DATETIME | - | NOT NULL | 过期时间 |
| created_at | DATETIME | - | NOT NULL | 创建时间 |

---

## 项目结构

```
loginSystem/
├── frontend/                 # 前端项目
│   ├── public/
│   │   └── vite-env.d.ts
│   ├── src/
│   │   ├── assets/          # 静态资源
│   │   ├── components/      # React 组件
│   │   │   ├── LoginForm/   # 登录表单组件
│   │   │   ├── ForgotPassword/  # 忘记密码组件
│   │   │   └── ResetPassword/   # 重置密码组件
│   │   ├── pages/           # 页面组件
│   │   │   ├── Login.tsx
│   │   │   ├── Dashboard.tsx
│   │   │   └── ResetPassword.tsx
│   │   ├── services/        # API 服务
│   │   │   └── auth.ts
│   │   ├── hooks/           # 自定义 Hooks
│   │   ├── utils/           # 工具函数
│   │   │   └── validation.ts
│   │   ├── types/           # TypeScript 类型
│   │   ├── App.tsx
│   │   └── main.tsx
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
├── backend/                  # 后端项目（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── employee/
│   │   │   │           └── auth/
│   │   │   │               ├── AuthApplication.java  # 启动类
│   │   │   │               ├── controller/          # 控制器层
│   │   │   │               │   └── AuthController.java
│   │   │   │               ├── service/             # 业务逻辑层
│   │   │   │               │   ├── AuthService.java
│   │   │   │               │   └── impl/
│   │   │   │               │       └── AuthServiceImpl.java
│   │   │   │               ├── repository/          # 数据访问层
│   │   │   │               │   └── UserRepository.java
│   │   │   │               ├── entity/              # 实体类
│   │   │   │               │   ├── User.java
│   │   │   │               │   └── PasswordReset.java
│   │   │   │               ├── dto/                 # 数据传输对象
│   │   │   │               │   ├── LoginRequest.java
│   │   │   │               │   ├── LoginResponse.java
│   │   │   │               │   └── ForgotPasswordRequest.java
│   │   │   │               ├── config/              # 配置类
│   │   │   │               │   ├── SecurityConfig.java
│   │   │   │               │   ├── JwtConfig.java
│   │   │   │               │   └── EmailConfig.java
│   │   │   │               ├── security/            # 安全相关
│   │   │   │               │   ├── JwtTokenProvider.java
│   │   │   │               │   ├── JwtAuthenticationFilter.java
│   │   │   │               │   └── UserDetailsServiceImpl.java
│   │   │   │               ├── exception/           # 异常处理
│   │   │   │               │   ├── GlobalExceptionHandler.java
│   │   │   │               │   └── AuthException.java
│   │   │   │               └── util/               # 工具类
│   │   │   │                   └── EmailUtil.java
│   │   │   └── resources/
│   │   │       ├── application.yml          # 应用配置
│   │   │       ├── application-dev.yml      # 开发环境配置
│   │   │       └── application-prod.yml     # 生产环境配置
│   │   └── test/
│   │       └── java/                          # 测试代码
│   ├── pom.xml                                # Maven 配置
│   └── .mvn/
│
└── database/                 # 数据库
    └── migrations/          # SQL 迁移文件
        └── V1__init_schema.sql
```

---

## API 接口设计

### 认证相关接口

| 方法 | 路径 | 说明 | 请求体 | 响应 |
|------|------|------|--------|------|
| POST | `/api/auth/login` | 用户登录 | `{ username, password }` | `{ token, user }` |
| POST | `/api/auth/logout` | 用户登出 | - | `{ message }` |
| POST | `/api/auth/forgot-password` | 忘记密码 | `{ email }` | `{ message }` |
| POST | `/api/auth/reset-password` | 重置密码 | `{ token, newPassword }` | `{ message }` |
| GET | `/api/auth/me` | 获取当前用户 | Header: Authorization | `{ user }` |

### 响应格式

**成功响应**:
```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIs...",
    "user": {
      "id": 1,
      "username": "admin",
      "email": "admin@example.com"
    }
  }
}
```

**错误响应**:
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "用户名或密码错误"
  }
}
```

---

## 安全措施

### 密码安全
- 使用 Spring Security BCrypt 加密存储密码（strength: 10）
- 密码强度验证（至少 8 位，包含字母和数字）

### Token 安全
- JWT 签名验证
- Token 过期时间设置（1 小时）
- Refresh Token 机制（7 天有效期）

### 防护措施
- 登录失败次数限制（5 次后锁定 15 分钟）
- SQL 注入防护（JPA 参数化查询）
- XSS 防护（输入验证和转义）
- CSRF 防护（Spring Security Token）
- CORS 跨域配置

---

## 开发阶段

### Phase 1: 项目初始化
- [ ] 初始化前端项目（Vite + React + TypeScript）
- [ ] 初始化后端项目（Spring Boot + Maven）
- [ ] 配置数据库连接
- [ ] 创建数据库表结构

### Phase 2: 后端开发
- [ ] 实现 Entity 类（User、PasswordReset）
- [ ] 实现 Repository 接口
- [ ] 实现 Service 业务逻辑
- [ ] 实现 Controller 接口
- [ ] 配置 Spring Security
- [ ] 实现 JWT 工具类
- [ ] 实现忘记密码邮件发送
- [ ] 实现全局异常处理

### Phase 3: 前端开发
- [ ] 实现登录页面 UI
- [ ] 实现表单验证
- [ ] 实现登录 API 调用
- [ ] 实现忘记密码页面
- [ ] 实现密码重置页面
- [ ] 实现登录成功页面

### Phase 4: 联调测试
- [ ] 前后端联调
- [ ] 功能测试
- [ ] 安全测试

### Phase 5: 部署上线
- [ ] 构建前端项目
- [ ] 打包后端 JAR
- [ ] 配置生产环境
- [ ] 部署上线

---

## 环境变量配置

### 后端 (application.yml)
```yaml
# 服务器配置
server:
  port: 8080
  servlet:
    context-path: /api

# Spring 配置
spring:
  application:
    name: employee-auth

  # 数据源配置
  datasource:
    url: jdbc:mysql://localhost:3306/employee_system?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver

  # JPA 配置
  jpa:
    hibernate:
      ddl-auto: update
    show-sql: true
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQLDialect

  # 邮件配置
  mail:
    host: smtp.gmail.com
    port: 587
    username: your_email@gmail.com
    password: your_app_password
    properties:
      mail:
        smtp:
          auth: true
          starttls:
            enable: true

# JWT 配置
jwt:
  secret: your_jwt_secret_key_here_at_least_256_bits
  expiration: 3600000  # 1 小时（毫秒）
  refresh-expiration: 604800000  # 7 天（毫秒）

# 日志配置
logging:
  level:
    com.employee.auth: DEBUG
    org.springframework.security: DEBUG
```

### 后端 (application-prod.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://prod-db-server:3306/employee_system?useSSL=true
    username: ${DB_USERNAME}
    password: ${DB_PASSWORD}

  jpa:
    show-sql: false

jwt:
  secret: ${JWT_SECRET}

logging:
  level:
    com.employee.auth: INFO
    org.springframework.security: WARN
```

### 前端 (.env)
```env
VITE_API_BASE_URL=http://localhost:3000/api
```

---

## 运行命令

### 后端 (Maven)
```bash
# 清理并编译
mvn clean compile

# 运行测试
mvn test

# 打包
mvn clean package

# 跳过测试打包
mvn clean package -DskipTests

# 运行应用
mvn spring-boot:run

# 指定环境运行
mvn spring-boot:run -Dspring-boot.run.profiles=dev

# 运行 JAR 包
java -jar target/employee-auth-1.0.0.jar
```

### 前端
```bash
# 安装依赖
npm install

# 开发模式
npm run dev

# 构建生产
npm run build

# 预览生产
npm run preview
```

---

## 参考资料

### 前端
- [React 官方文档](https://react.dev/)
- [Vite 官方文档](https://vitejs.dev/)
- [React Router 文档](https://reactrouter.com/)

### 后端
- [Spring Boot 官方文档](https://spring.io/projects/spring-boot)
- [Spring Security 参考](https://docs.spring.io/spring-security/reference/)
- [Spring Data JPA 文档](https://spring.io/projects/spring-data-jpa)
- [JJWT 文档](https://github.com/jwtk/jjwt)

### 其他
- [JWT 官网](https://jwt.io/)
- [MySQL 参考手册](https://dev.mysql.com/doc/)

---

*创建日期：2026-03-10*
*版本：2.0 (Java 后端)*
