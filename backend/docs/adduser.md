# 用户注册功能需求文档

## 1. 概述

### 1.1 功能简介
为员工认证系统添加用户注册功能，允许新用户创建账户。注册成功后自动登录并返回 JWT Token。

### 1.2 目标
- 提供安全、可靠的用户注册流程
- 防止重复注册（用户名和邮箱唯一性验证）
- 确保密码安全存储
- 提供友好的错误提示

---

## 2. 用户故事

| ID | 用户故事 | 优先级 |
|----|----------|--------|
| US-01 | 作为新用户，我希望能够通过用户名、邮箱和密码创建账户，以便访问系统功能 | P0 |
| US-02 | 作为新用户，我希望系统在注册前验证用户名和邮箱是否已被占用 | P0 |
| US-03 | 作为新用户，我希望系统对密码强度有要求，以保证账户安全 | P0 |
| US-04 | 作为新用户，我希望在注册成功后自动登录，无需再次输入凭据 | P1 |
| US-05 | 作为用户，我希望收到清晰的错误提示，以便我知道如何修正输入 | P1 |

---

## 3. UI/UX 设计

### 3.1 注册页面布局

```
+--------------------------------------------------+
|                   员工系统注册                    |
+--------------------------------------------------+
|                                                  |
|  用户名                                          |
|  [_______________________]                       |
|  * 用户名长度 3-20 个字符                        |
|                                                  |
|  邮箱                                            |
|  [_______________________]                       |
|  * 请输入有效的邮箱地址                          |
|                                                  |
|  密码                                            |
|  [_______________________]  (显示/隐藏)          |
|  * 至少 8 个字符，包含字母和数字                 |
|                                                  |
|  确认密码                                        |
|  [_______________________]  (显示/隐藏)          |
|  * 请再次输入密码                                |
|                                                  |
|  [ ] 我同意服务条款和隐私政策                    |
|                                                  |
|              [创建账户]                          |
|                                                  |
|  已有账户？[立即登录]                            |
|                                                  |
+--------------------------------------------------+
```

### 3.2 交互流程

```
用户访问注册页面
    |
    v
填写注册表单
    |
    v
点击"创建账户"
    |
    +---> 客户端验证
    |     |
    |     +---> 验证通过 ----> 发送注册请求
    |     |
    |     +---> 验证失败 ----> 显示错误提示
    |
    v
服务器处理
    |
    +---> 检查用户名是否存在
    |     |
    |     +---> 存在 ----> 返回错误："用户名已被占用"
    |     |
    |     +---> 不存在 ----> 继续
    |
    v
检查邮箱是否存在
    |
    +---> 存在 ----> 返回错误："邮箱已被注册"
    |
    +---> 不存在 ----> 继续
    |
    v
创建用户账户
    |
    v
生成 JWT Token
    |
    v
返回注册成功响应
    |
    v
自动登录，跳转到首页
```

### 3.3 表单验证规则

| 字段 | 验证规则 | 错误提示 |
|------|----------|----------|
| 用户名 | 必填，3-20 个字符，仅限字母、数字、下划线 | 用户名长度 3-20 个字符，只能包含字母、数字和下划线 |
| 邮箱 | 必填，有效邮箱格式 | 请输入有效的邮箱地址 |
| 密码 | 必填，至少 8 个字符，包含字母和数字 | 密码至少 8 个字符，必须包含字母和数字 |
| 确认密码 | 必填，与密码一致 | 两次输入的密码不一致 |

### 3.4 API 响应格式

**成功响应：**
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

**失败响应：**
```json
{
  "success": false,
  "error": {
    "code": "USERNAME_EXISTS",
    "message": "用户名已被占用"
  }
}
```

---

## 4. 技术规范

### 4.1 数据模型

**现有 User 实体（无需修改）：**
```java
@Entity
@Table(name = "users")
public class User {
    private Long id;
    private String username;   // 唯一
    private String password;   // BCrypt 加密
    private String email;      // 唯一
    private String fullName;
    private Boolean isActive;  // 默认 true
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### 4.2 API 端点

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| POST | /api/auth/register | 用户注册 | 否 |

### 4.3 请求/响应 DTO

**RegisterRequest.java：**
```java
public class RegisterRequest {
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 20, message = "用户名长度 3-20 个字符")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "用户名只能包含字母、数字和下划线")
    private String username;

    @NotBlank(message = "邮箱不能为空")
    @Email(message = "请输入有效的邮箱地址")
    private String email;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8, message = "密码至少 8 个字符")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$", message = "密码必须包含字母和数字")
    private String password;

    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
```

**RegisterResponse.java（复用 LoginResponse 结构）：**
```java
// 直接使用 LoginResponse 作为注册响应
```

### 4.4 业务逻辑

1. **验证密码一致性**：检查 `password` 和 `confirmPassword` 是否一致
2. **检查用户名唯一性**：调用 `userRepository.existsByUsername()`
3. **检查邮箱唯一性**：调用 `userRepository.existsByEmail()`
4. **创建用户**：
   - 使用 `passwordEncoder.encode()` 加密密码
   - 设置 `isActive = true`
   - 保存到数据库
5. **生成 Token**：调用 `jwtTokenProvider.generateToken()`
6. **返回响应**：返回 Token 和用户信息

### 4.5 错误码定义

| 错误码 | 描述 |
|--------|------|
| USERNAME_EXISTS | 用户名已被占用 |
| EMAIL_EXISTS | 邮箱已被注册 |
| PASSWORD_MISMATCH | 两次输入的密码不一致 |

---

## 5. 实施计划

### 阶段一：DTO 和接口定义（30 分钟）

| 任务 | 文件 | 描述 |
|------|------|------|
| 1.1 | `RegisterRequest.java` | 创建注册请求 DTO，添加验证注解 |
| 1.2 | `AuthService.java` | 添加 `register()` 方法签名 |

### 阶段二：业务逻辑实现（45 分钟）

| 任务 | 文件 | 描述 |
|------|------|------|
| 2.1 | `AuthServiceImpl.java` | 实现 `register()` 方法 |

### 阶段三：控制器和配置（30 分钟）

| 任务 | 文件 | 描述 |
|------|------|------|
| 3.1 | `AuthController.java` | 添加 `/register` 端点 |
| 3.2 | `SecurityConfig.java` | 允许 `/auth/register` 无需认证 |

### 阶段四：测试（45 分钟）

| 任务 | 描述 |
|------|------|
| 4.1 | 单元测试：正常注册流程 |
| 4.2 | 单元测试：用户名重复 |
| 4.3 | 单元测试：邮箱重复 |
| 4.4 | 单元测试：密码不一致 |
| 4.5 | 集成测试：完整注册流程 |

---

## 6. 代码实现清单

### 新建文件

| 文件路径 | 描述 |
|----------|------|
| `src/main/java/com/employee/auth/dto/RegisterRequest.java` | 注册请求 DTO |

### 修改文件

| 文件路径 | 修改内容 |
|----------|----------|
| `src/main/java/com/employee/auth/service/AuthService.java` | 添加 `register()` 方法 |
| `src/main/java/com/employee/auth/service/impl/AuthServiceImpl.java` | 实现 `register()` 方法 |
| `src/main/java/com/employee/auth/controller/AuthController.java` | 添加 `/register` 端点 |
| `src/main/java/com/employee/auth/config/SecurityConfig.java` | 更新安全配置 |

---

## 7. 测试用例

### 7.1 正常注册测试

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

**预期结果：** 返回 200 状态码和包含 Token 的响应

### 7.2 用户名重复测试

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

**预期结果：** 返回 400 状态码，错误码 `USERNAME_EXISTS`

### 7.3 邮箱重复测试

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

**预期结果：** 返回 400 状态码，错误码 `EMAIL_EXISTS`

### 7.4 密码不一致测试

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

**预期结果：** 返回 400 状态码，错误码 `PASSWORD_MISMATCH`

### 7.5 表单验证测试

| 字段 | 测试值 | 预期结果 |
|------|--------|----------|
| username | ""（空字符串） | 用户名不能为空 |
| username | "ab"（少于 3 个字符） | 用户名长度 3-20 个字符 |
| username | "user@name"（包含特殊字符） | 用户名只能包含字母、数字和下划线 |
| email | "invalid-email" | 请输入有效的邮箱地址 |
| password | "123456"（少于 8 个字符） | 密码至少 8 个字符 |
| password | "abcdefgh"（纯字母） | 密码必须包含字母和数字 |

---

## 8. 验收标准

- [ ] 用户可以通过用户名、邮箱、密码成功注册
- [ ] 注册后自动登录并获取有效 Token
- [ ] 重复用户名或邮箱时给出明确错误提示
- [ ] 密码以 BCrypt 加密形式存储
- [ ] 新用户账户默认激活状态
- [ ] API 响应符合统一格式
- [ ] 所有验证规则生效
- [ ] 单元测试覆盖率 > 80%

---

## 9. 未来扩展

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 邮箱验证 | 注册后发送验证邮件，激活账户 | P1 |
| 图形验证码 | 防止机器人批量注册 | P2 |
| 手机号注册 | 支持手机号作为登录账号 | P2 |
| 第三方登录 | 支持微信、企业微信等第三方登录 | P3 |
| 用户角色 | 注册时分配默认角色，后续扩展权限系统 | P1 |

---

*文档版本：1.0*
*创建日期：2026-03-10*
*最后更新：2026-03-10*
