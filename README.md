# 员工登录系统

一个完整的员工登录系统，包含前端（React + TypeScript）和后端（Spring Boot + Java）。

## 项目结构

```
loginSystem/
├── frontend/           # 前端项目（React + TypeScript + Vite）
├── backend/            # 后端项目（Spring Boot + Java）
└── database/           # 数据库 SQL 文件
```

## 技术栈

### 前端
- React 18
- TypeScript 5
- Vite 5
- Axios

### 后端
- Java 17
- Spring Boot 3.2.x
- Spring Security
- Spring Data JPA
- MySQL
- JWT

## 快速开始

### 1. 数据库初始化

执行 SQL 文件创建数据库和表：

```bash
mysql -u root -p < database/migrations/V1__init_schema.sql
```

### 2. 后端启动

```bash
cd backend

# 修改配置文件
# 编辑 src/main/resources/application.yml 中的数据库密码

# 运行项目
mvn spring-boot:run
```

后端服务启动在：`http://localhost:8080/api`

### 3. 前端启动

```bash
cd frontend

# 安装依赖
npm install

# 启动开发服务器
npm run dev
```

前端服务启动在：`http://localhost:5173`

## 默认账户

- 用户名：`admin`
- 密码：`admin123`

## 功能

- ✅ 用户登录
- ✅ JWT 认证
- ✅ 忘记密码
- ✅ 密码重置
- ✅ 表单验证

## API 接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/auth/login` | POST | 用户登录 |
| `/api/auth/logout` | POST | 用户登出 |
| `/api/auth/forgot-password` | POST | 忘记密码 |
| `/api/auth/reset-password` | POST | 重置密码 |
| `/api/auth/me` | GET | 获取当前用户 |

## 开发规范

- 遵循 ES 模块语法
- 使用 TypeScript 类型检查
- 中文注释
- JSDoc/Javadoc 格式

## 许可证

MIT
