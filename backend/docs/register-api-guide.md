# 注册 API 使用指南

## API 端点

### 用户注册

**请求：**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "John123456",
  "confirmPassword": "John123456"
}
```

**成功响应（200 OK）：**

```json
{
  "success": true,
  "data": {
    "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "type": "Bearer",
    "user": {
      "id": 1,
      "username": "john_doe",
      "email": "john@example.com",
      "fullName": null
    }
  }
}
```

**失败响应（400 Bad Request）：**

```json
{
  "success": false,
  "error": {
    "code": "REGISTRATION_FAILED",
    "message": "用户名已被占用"
  }
}
```

## cURL 测试示例

### 1. 正常注册

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'
```

### 2. 用户名重复

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "new@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'
```

### 3. 邮箱重复

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "admin@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'
```

### 4. 密码不一致

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "newuser",
    "email": "new@example.com",
    "password": "Test123456",
    "confirmPassword": "Test654321"
  }'
```

### 5. 表单验证失败

```bash
# 用户名太短
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "ab",
    "email": "test@example.com",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'

# 密码太短
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "123456",
    "confirmPassword": "123456"
  }'

# 邮箱格式无效
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "invalid-email",
    "password": "Test123456",
    "confirmPassword": "Test123456"
  }'
```

## 验证规则

| 字段 | 规则 |
|------|------|
| username | 3-20 个字符，仅限字母、数字、下划线 |
| email | 有效邮箱格式 |
| password | 至少 8 个字符，必须包含字母和数字 |
| confirmPassword | 必须与 password 一致 |
