# 员工登录系统 - 启动指南

## 📋 当前状态

✅ 前端依赖已安装
✅ 前端服务已启动（http://localhost:5173）
⏳ 后端服务需要手动启动

---

## 🚀 完整启动步骤

### 第一步：初始化数据库

**方式1：使用命令行**
```bash
# Windows PowerShell
mysql -u root -p < k:/Cursor/loginSystem/database/migrations/V1__init_schema.sql

# 或手动执行 SQL 文件
```

**方式2：使用 MySQL Workbench**
1. 打开 MySQL Workbench
2. 连接到本地 MySQL 服务器
3. 打开文件 `k:/Cursor/loginSystem/database/migrations/V1__init_schema.sql`
4. 执行 SQL 脚本

### 第二步：配置数据库密码

编辑文件：`backend/src/main/resources/application.yml`

```yaml
spring:
  datasource:
    password: your_password  # 改为你的 MySQL 密码
```

### 第三步：启动后端服务

**方式1：使用 Maven（推荐）**
```bash
cd k:/Cursor/loginSystem/backend
mvn spring-boot:run
```

**方式2：使用 IDE**
- 用 IntelliJ IDEA 打开 `backend` 目录
- 运行 `AuthApplication.java`

**方式3：先打包再运行**
```bash
cd k:/Cursor/loginSystem/backend
mvn clean package
java -jar target/employee-auth-1.0.0.jar
```

后端启动成功后，服务运行在：`http://localhost:8080/api`

### 第四步：启动前端服务

前端已启动，如需重新启动：

```bash
cd k:/Cursor/loginSystem/frontend
npm run dev
```

前端运行在：`http://localhost:5173`

---

## 🔑 默认账户

- 用户名：`admin`
- 密码：`admin123`

---

## 📝 API 接口测试

使用 Postman 或 curl 测试后端接口：

### 登录
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### 获取当前用户
```bash
curl -X GET http://localhost:8080/api/auth/me \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

## ⚠️ 常见问题

### 1. Maven 命令不可用

**解决方案：** 将 Maven 的 bin 目录添加到系统 PATH

```bash
# Maven 常见安装路径：
# D:\WorkTool\apache-maven-3.6.0\bin
# D:\UtilitySoftware\Maven\bin
```

### 2. MySQL 连接失败

**解决方案：**
- 检查 MySQL 服务是否启动
- 确认用户名和密码正确
- 检查防火墙设置

### 3. 端口冲突

**解决方案：** 修改 `application.yml` 中的端口号

```yaml
server:
  port: 8081  # 改为其他端口
```

---

## 📦 项目依赖

### 前端依赖已安装
- react
- react-dom
- react-router-dom
- axios
- typescript
- vite
- ...

### 后端依赖
Maven 会自动下载以下依赖：
- spring-boot-starter-web
- spring-boot-starter-security
- spring-boot-starter-data-jpa
- spring-boot-starter-mail
- mysql-connector-j
- jjwt
- lombok

---

## 🎯 访问地址

| 服务 | 地址 |
|------|------|
| 前端 | http://localhost:5173 |
| 后端 API | http://localhost:8080/api |
| 仪表板 | http://localhost:5173/dashboard.html |

---

*更新时间：2026-03-10*
