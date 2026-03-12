# 用户信息 API 测试指南

## API 接口列表

### 基础信息

- **Base URL**: `http://localhost:8080/api`
- **认证方式**: Bearer Token (JWT)
- **Content-Type**: `application/json`

---

## 1. 获取当前用户信息

### 接口信息

- **URL**: `/api/users/me`
- **方法**: `GET`
- **认证**: 必须

### 请求示例

```bash
curl -X GET http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json"
```

### 响应示例

**成功 (200 OK):**
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsan@example.com",
    "fullName": "张三",
    "isActive": true,
    "createdAt": "2026-03-10T12:00:00",
    "updatedAt": "2026-03-10T12:00:00"
  }
}
```

**未认证 (401 Unauthorized):**
```json
{
  "success": false,
  "error": {
    "code": "UNAUTHORIZED",
    "message": "未认证"
  }
}
```

---

## 2. 更新用户信息

### 接口信息

- **URL**: `/api/users/me`
- **方法**: `PUT`
- **认证**: 必须

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| fullName | String | 否 | 全名，长度1-100 |
| email | String | 否 | 邮箱，必须是有效邮箱格式 |

### 请求示例

```bash
curl -X PUT http://localhost:8080/api/users/me \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "张三丰",
    "email": "zhangsanfeng@example.com"
  }'
```

### 响应示例

**成功 (200 OK):**
```json
{
  "success": true,
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "username": "zhangsan",
    "email": "zhangsanfeng@example.com",
    "fullName": "张三丰",
    "isActive": true,
    "createdAt": "2026-03-10T12:00:00",
    "updatedAt": "2026-03-10T15:30:00"
  }
}
```

**参数验证失败 (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_FAILED",
    "message": "参数验证失败",
    "details": {
      "email": "邮箱格式不正确"
    }
  }
}
```

**邮箱已被使用 (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "ILLEGAL_ARGUMENT",
    "message": "该邮箱已被其他用户使用"
  }
}
```

---

## 3. 修改密码

### 接口信息

- **URL**: `/api/users/me/password`
- **方法**: `PUT`
- **认证**: 必须

### 请求体

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| currentPassword | String | 是 | 当前密码 |
| newPassword | String | 是 | 新密码，长度6-50 |
| confirmPassword | String | 是 | 确认新密码 |

### 请求示例

```bash
curl -X PUT http://localhost:8080/api/users/me/password \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "currentPassword": "oldPassword123",
    "newPassword": "newPassword456",
    "confirmPassword": "newPassword456"
  }'
```

### 响应示例

**成功 (200 OK):**
```json
"密码修改成功"
```

**当前密码不正确 (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_PASSWORD",
    "message": "当前密码不正确"
  }
}
```

**两次密码不一致 (400 Bad Request):**
```json
{
  "success": false,
  "error": {
    "code": "ILLEGAL_ARGUMENT",
    "message": "两次输入的新密码不一致"
  }
}
```

---

## Postman 测试集合

可以导入以下 JSON 作为 Postman Collection:

```json
{
  "info": {
    "name": "员工认证系统 API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "http://localhost:8080/api"
    },
    {
      "key": "token",
      "value": "YOUR_JWT_TOKEN"
    }
  ],
  "item": [
    {
      "name": "用户管理",
      "item": [
        {
          "name": "获取当前用户信息",
          "request": {
            "method": "GET",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              }
            ],
            "url": {
              "raw": "{{baseUrl}}/users/me",
              "host": ["{{baseUrl}}"],
              "path": ["users", "me"]
            }
          }
        },
        {
          "name": "更新用户信息",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"fullName\": \"新名称\",\n  \"email\": \"new@example.com\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/users/me",
              "host": ["{{baseUrl}}"],
              "path": ["users", "me"]
            }
          }
        },
        {
          "name": "修改密码",
          "request": {
            "method": "PUT",
            "header": [
              {
                "key": "Authorization",
                "value": "Bearer {{token}}"
              },
              {
                "key": "Content-Type",
                "value": "application/json"
              }
            ],
            "body": {
              "mode": "raw",
              "raw": "{\n  \"currentPassword\": \"oldPassword\",\n  \"newPassword\": \"newPassword123\",\n  \"confirmPassword\": \"newPassword123\"\n}"
            },
            "url": {
              "raw": "{{baseUrl}}/users/me/password",
              "host": ["{{baseUrl}}"],
              "path": ["users", "me", "password"]
            }
          }
        }
      ]
    }
  ]
}
```

---

## 错误代码说明

| 代码 | 说明 |
|------|------|
| RESOURCE_NOT_FOUND | 资源不存在（如用户不存在） |
| INVALID_PASSWORD | 密码错误（如当前密码不正确） |
| VALIDATION_FAILED | 参数验证失败 |
| ILLEGAL_ARGUMENT | 非法参数 |
| UNAUTHORIZED | 未认证 |
| INTERNAL_ERROR | 服务器内部错误 |
