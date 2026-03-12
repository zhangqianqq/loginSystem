# 用户信息显示功能 - 实现计划

## 项目概述

本文档详细规划 Spring Boot 员工认证系统中的**用户信息显示功能**实现方案。该功能允许已登录用户查看和管理自己的个人信息。

### 项目背景

- **项目名称**: 员工登录系统 (Employee Authentication System)
- **技术栈**: Spring Boot 3.2.2 + Spring Security + JWT + JPA + MySQL
- **包路径**: `com.employee.auth`
- **API 前缀**: `/api`
- **当前功能**: 登录、注册、忘记密码、重置密码

### 现有项目结构

```
backend/src/main/java/com/employee/auth/
├── config/                    # 配置类
│   ├── JwtConfig.java        # JWT 配置
│   └── SecurityConfig.java   # Spring Security 配置
├── controller/               # 控制器层
│   └── AuthController.java   # 认证控制器
├── dto/                      # 数据传输对象
│   ├── ApiResponse.java      # 通用响应
│   ├── LoginRequest.java     # 登录请求
│   ├── LoginResponse.java    # 登录响应
│   ├── RegisterRequest.java  # 注册请求
│   ├── ForgotPasswordRequest.java
│   └── ResetPasswordRequest.java
├── entity/                   # 实体类
│   ├── User.java            # 用户实体
│   └── PasswordReset.java   # 密码重置实体
├── repository/               # 数据访问层
│   ├── UserRepository.java
│   └── PasswordResetRepository.java
├── security/                 # 安全组件
│   └── JwtTokenProvider.java # JWT 工具类
├── service/                  # 服务接口
│   └── AuthService.java
└── service/impl/             # 服务实现
    └── AuthServiceImpl.java
```

---

## 1. 功能需求分析

### 1.1 核心功能

| 功能 | 描述 | 优先级 |
|------|------|--------|
| 获取当前用户信息 | 从 JWT Token 解析用户ID，返回完整的用户资料 | P0（必须） |
| 更新用户资料 | 允许用户修改全名、邮箱等个人信息 | P1（重要） |
| 修改密码 | 用户在登录状态下修改密码 | P1（重要） |
| 上传头像 | 用户头像上传和管理 | P2（可选） |
| 账户状态查询 | 显示账户激活状态、注册时间等 | P1（重要） |

### 1.2 用户信息字段

当前 `User` 实体包含以下字段：

```java
@Entity
@Table(name = "users")
public class User {
    private Long id;              // 用户ID（主键）
    private String username;      // 用户名（唯一，不可修改）
    private String password;      // 加密密码（不返回前端）
    private String email;         // 邮箱（唯一）
    private String fullName;      // 全名
    private Boolean isActive;     // 是否激活
    private LocalDateTime createdAt;  // 创建时间
    private LocalDateTime updatedAt;  // 更新时间
}
```

---

## 2. 技术架构设计

### 2.1 数据流图

```
┌─────────┐      HTTP Request       ┌──────────────┐
│ Client  │ ──────────────────────> │ UserController │
└─────────┘   (JWT in Authorization) └──────────────┘
                                                │
                                                ▼
                                         ┌──────────────┐
                                         │ UserService  │
                                         └──────────────┘
                                                │
                                                ▼
                                         ┌──────────────┐
                                         │ UserRepository│
                                         └──────────────┘
                                                │
                                                ▼
                                         ┌──────────────┐
                                         │   Database   │
                                         └──────────────┘
```

### 2.2 安全设计

#### JWT Token 验证流程

```mermaid
sequenceDiagram
    participant Client
    participant UserController
    participant JwtTokenProvider
    participant UserService

    Client->>UserController: GET /api/users/me<br/>Authorization: Bearer {token}
    UserController->>JwtTokenProvider: validateToken(token)
    JwtTokenProvider-->>UserController: userId
    UserController->>UserService: getUserById(userId)
    UserService-->>UserController: User
    UserController-->>Client: UserInfo Response
```

#### 安全考虑

| 考虑点 | 实现方式 |
|--------|----------|
| 敏感数据保护 | 密码字段永不返回前端 |
| Token 验证 | 所有接口验证 JWT 有效性 |
| 权限控制 | 用户只能访问/修改自己的信息 |
| 输入验证 | 使用 `@Valid` 进行参数校验 |

---

## 3. API 接口设计

### 3.1 用户控制器 API

| 方法 | 路径 | 描述 | 认证 |
|------|------|------|------|
| GET | `/api/users/me` | 获取当前用户信息 | 必须 |
| PUT | `/api/users/me` | 更新当前用户信息 | 必须 |
| PUT | `/api/users/me/password` | 修改密码 | 必须 |
| POST | `/api/users/me/avatar` | 上传头像 | 必须 |

### 3.2 DTO 设计

#### 3.2.1 用户信息响应 DTO

```java
/**
 * 用户信息响应 DTO
 * 不包含敏感信息（密码等）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 全名
     */
    private String fullName;

    /**
     * 账户是否激活
     */
    private Boolean isActive;

    /**
     * 注册时间
     */
    private LocalDateTime createdAt;

    /**
     * 最后更新时间
     */
    private LocalDateTime updatedAt;
}
```

#### 3.2.2 更新用户请求 DTO

```java
/**
 * 更新用户信息请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    /**
     * 全名（可选）
     */
    @Size(min = 1, max = 100, message = "全名长度必须在1-100之间")
    private String fullName;

    /**
     * 邮箱（可选，修改需要验证）
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;
}
```

#### 3.2.3 修改密码请求 DTO

```java
/**
 * 修改密码请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordRequest {

    /**
     * 当前密码
     */
    @NotBlank(message = "当前密码不能为空")
    private String currentPassword;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 50, message = "密码长度必须在6-50之间")
    private String newPassword;

    /**
     * 确认新密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;
}
```

### 3.3 API 响应示例

#### GET /api/users/me

**请求头:**
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

**响应 (200 OK):**
```json
{
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

#### PUT /api/users/me

**请求体:**
```json
{
  "fullName": "张三丰",
  "email": "zhangsanfeng@example.com"
}
```

**响应 (200 OK):**
```json
{
  "code": 200,
  "message": "用户信息更新成功",
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

#### PUT /api/users/me/password

**请求体:**
```json
{
  "currentPassword": "oldPassword123",
  "newPassword": "newPassword456",
  "confirmPassword": "newPassword456"
}
```

**响应 (200 OK):**
```json
{
  "code": 200,
  "message": "密码修改成功",
  "data": null
}
```

---

## 4. 实现方案

### 4.1 文件结构规划

需要创建/修改的文件：

```
backend/src/main/java/com/employee/auth/
├── controller/
│   └── UserController.java           # 新增：用户控制器
├── dto/
│   ├── UserInfoResponse.java         # 新增：用户信息响应
│   ├── UpdateUserRequest.java        # 新增：更新用户请求
│   └── ChangePasswordRequest.java    # 新增：修改密码请求
├── service/
│   └── UserService.java              # 新增：用户服务接口
├── service/impl/
│   └── UserServiceImpl.java          # 新增：用户服务实现
└── exception/
    ├── ResourceNotFoundException.java   # 新增：资源未找到异常
    └── InvalidPasswordException.java    # 新增：密码错误异常
```

### 4.2 安全配置更新

需要更新 `SecurityConfig.java`，添加用户相关接口的访问控制：

```java
.authorizeHttpRequests(auth -> auth
    // 公开接口
    .requestMatchers("/auth/login", "/auth/register", "/auth/forgot-password", "/auth/reset-password").permitAll()
    // 测试接口
    .requestMatchers("/test/**").permitAll()
    // OPTIONS 请求
    .requestMatchers(request -> "OPTIONS".equals(request.getMethod())).permitAll()
    // 用户相关接口需要认证
    .requestMatchers("/users/**").authenticated()
    // 其他请求需要认证
    .anyRequest().authenticated()
)
```

### 4.3 JWT Token 提取增强

创建 `JwtAuthenticationFilter` 来自动从请求中提取用户信息：

```java
/**
 * JWT 认证过滤器
 * 从请求头中提取 JWT Token 并设置到 SecurityContext
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) {
        // 从请求头获取 Token
        String token = extractToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            Long userId = jwtTokenProvider.getUserIdFromToken(token);
            // 设置到请求属性中，供控制器使用
            request.setAttribute("currentUserId", userId);
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## 5. 实现步骤

### 第一阶段：基础功能（P0）- 已完成 ✅

- [x] 创建用户信息相关 DTO
  - [x] `UserInfoResponse.java`
  - [x] `UpdateUserRequest.java`
  - [x] `ChangePasswordRequest.java`

- [x] 创建用户服务层
  - [x] `UserService.java` 接口
  - [x] `UserServiceImpl.java` 实现
  - [x] 实现 `getUserById()` 方法
  - [x] 实现 `updateUser()` 方法

- [x] 创建用户控制器
  - [x] `UserController.java`
  - [x] 实现 `GET /api/users/me` 接口
  - [x] 实现 `PUT /api/users/me` 接口

- [x] 更新安全配置
  - [x] 添加 `/users/**` 路径的认证规则

### 第二阶段：密码管理（P1）- 已完成 ✅

- [x] 实现修改密码功能
  - [x] `changePassword()` 服务方法
  - [x] `PUT /api/users/me/password` 接口
  - [x] 验证当前密码逻辑

- [x] 创建自定义异常
  - [x] `ResourceNotFoundException.java`
  - [x] `InvalidPasswordException.java`
  - [x] 全局异常处理器 `GlobalExceptionHandler.java`

### 第三阶段：增强功能（P2）- 待实现

- [ ] 头像上传功能
  - [ ] 文件存储配置
  - [ ] 图片上传接口
  - [ ] 头像访问接口

- [ ] 账户状态管理
  - [ ] 最后登录时间记录
  - [ ] 登录历史记录

---

## 5.1 已实现的文件清单

### 新增文件

| 文件路径 | 描述 |
|----------|------|
| `dto/UserInfoResponse.java` | 用户信息响应 DTO |
| `dto/UpdateUserRequest.java` | 更新用户请求 DTO |
| `dto/ChangePasswordRequest.java` | 修改密码请求 DTO |
| `exception/ResourceNotFoundException.java` | 资源未找到异常 |
| `exception/InvalidPasswordException.java` | 密码错误异常 |
| `exception/GlobalExceptionHandler.java` | 全局异常处理器 |
| `service/UserService.java` | 用户服务接口 |
| `service/impl/UserServiceImpl.java` | 用户服务实现 |
| `controller/UserController.java` | 用户控制器 |
| `test/service/UserServiceTest.java` | 用户服务测试类 |

### 修改文件

| 文件路径 | 修改内容 |
|----------|----------|
| `dto/ApiResponse.java` | 添加 code、message 字段和带 details 的 error 方法 |
| `repository/UserRepository.java` | 添加 `existsByEmailAndIdNot()` 方法 |
| `config/SecurityConfig.java` | 添加 `/users/**` 认证规则 |
| `controller/AuthController.java` | 更新 `getCurrentUser()` 使用新的 UserService |

---

## 5.2 核心代码位置

### 1. 用户控制器
**文件**: `K:\Cursor\loginSystem\backend\src\main\java\com\employee\auth\controller\UserController.java`

```java
@RestController
@RequestMapping("/users")
public class UserController {

    // GET /api/users/me - 获取当前用户信息
    @GetMapping("/me")

    // PUT /api/users/me - 更新当前用户信息
    @PutMapping("/me")

    // PUT /api/users/me/password - 修改密码
    @PutMapping("/me/password")
}
```

### 2. 用户服务
**文件**: `K:\Cursor\loginSystem\backend\src\main\java\com\employee\auth\service\impl\UserServiceImpl.java`

```java
@Service
public class UserServiceImpl implements UserService {

    // 根据ID获取用户信息
    UserInfoResponse getUserById(Long userId)

    // 更新用户信息
    UserInfoResponse updateUser(Long userId, UpdateUserRequest request)

    // 修改密码
    void changePassword(Long userId, ChangePasswordRequest request)
}
```

### 3. 异常处理
**文件**: `K:\Cursor\loginSystem\backend\src\main\java\com\employee\auth\exception\GlobalExceptionHandler.java`

统一处理以下异常：
- `ResourceNotFoundException` - 资源不存在（404）
- `InvalidPasswordException` - 密码错误（400）
- `MethodArgumentNotValidException` - 参数验证失败（400）
- `IllegalArgumentException` - 非法参数（400）
- `RuntimeException` - 运行时异常（500）

---

## 5.3 测试文件

**文件**: `K:\Cursor\loginSystem\backend\src\test\java\com\employee\auth\service\UserServiceTest.java`

包含以下测试用例：
1. `getUserById_Success` - 成功获取用户信息
2. `getUserById_UserNotFound` - 用户不存在
3. `updateUser_UpdateFullName_Success` - 更新全名
4. `updateUser_UpdateEmail_Success` - 更新邮箱
5. `updateUser_UpdateEmail_AlreadyExists` - 邮箱已被使用
6. `changePassword_Success` - 修改密码成功
7. `changePassword_CurrentPasswordIncorrect` - 当前密码不正确
8. `changePassword_PasswordsNotMatch` - 两次密码不一致
9. `changePassword_NewPasswordSameAsCurrent` - 新密码与当前密码相同

---

## 6. 测试策略

### 6.1 单元测试

```java
@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @Test
    @DisplayName("根据ID获取用户信息 - 成功")
    void getUserById_Success() {
        // Given
        Long userId = 1L;
        User user = User.builder()
            .id(userId)
            .username("testuser")
            .email("test@example.com")
            .fullName("测试用户")
            .isActive(true)
            .build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // When
        UserInfoResponse response = userService.getUserById(userId);

        // Then
        assertThat(response.getId()).isEqualTo(userId);
        assertThat(response.getUsername()).isEqualTo("testuser");
        verify(userRepository).findById(userId);
    }

    @Test
    @DisplayName("根据ID获取用户信息 - 用户不存在")
    void getUserById_UserNotFound() {
        // Given
        Long userId = 999L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> userService.getUserById(userId))
            .isInstanceOf(ResourceNotFoundException.class)
            .hasMessage("用户不存在");
    }
}
```

### 6.2 集成测试

```java
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Test
    @DisplayName("获取当前用户信息 - 成功")
    void getCurrentUser_Success() throws Exception {
        // Given
        User user = createUser("testuser");
        String token = jwtTokenProvider.generateToken(user);

        // When & Then
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"));
    }

    @Test
    @DisplayName("获取当前用户信息 - 未认证")
    void getCurrentUser_Unauthorized() throws Exception {
        mockMvc.perform(get("/api/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
```

### 6.3 API 测试用例清单

| 接口 | 测试场景 | 预期结果 |
|------|----------|----------|
| GET /api/users/me | 有效 Token | 200 OK，返回用户信息 |
| GET /api/users/me | 无 Token | 401 Unauthorized |
| GET /api/users/me | 过期 Token | 401 Unauthorized |
| PUT /api/users/me | 更新全名 | 200 OK，更新成功 |
| PUT /api/users/me | 更新为已存在邮箱 | 400 Bad Request |
| PUT /api/users/me/password | 正确的当前密码 | 200 OK |
| PUT /api/users/me/password | 错误的当前密码 | 400 Bad Request |
| PUT /api/users/me/password | 两次密码不一致 | 400 Bad Request |

---

## 7. 前端集成说明

### 7.1 API 调用示例

```javascript
// 获取当前用户信息
async function getCurrentUser() {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/users/me', {
        method: 'GET',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        }
    });
    return await response.json();
}

// 更新用户信息
async function updateUserInfo(userData) {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/users/me', {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(userData)
    });
    return await response.json();
}

// 修改密码
async function changePassword(passwords) {
    const token = localStorage.getItem('token');
    const response = await fetch('http://localhost:8080/api/users/me/password', {
        method: 'PUT',
        headers: {
            'Authorization': `Bearer ${token}`,
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(passwords)
    });
    return await response.json();
}
```

### 7.2 用户状态管理

建议在前端使用状态管理（如 Context API、Redux 等）管理用户信息：

```javascript
// 用户上下文示例
const UserContext = React.createContext();

function UserProvider({ children }) {
    const [user, setUser] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        // 应用启动时获取用户信息
        fetchCurrentUser();
    }, []);

    const fetchCurrentUser = async () => {
        try {
            const data = await getCurrentUser();
            setUser(data.data);
        } catch (error) {
            console.error('获取用户信息失败', error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <UserContext.Provider value={{ user, loading, fetchCurrentUser }}>
            {children}
        </UserContext.Provider>
    );
}
```

---

## 8. 部署检查清单

### 8.1 配置检查

- [ ] JWT 密钥已正确配置
- [ ] 数据库连接正常
- [ ] CORS 配置包含前端域名
- [ ] 文件上传目录权限（如果实现头像功能）

### 8.2 安全检查

- [ ] 密码字段永不返回前端
- [ ] JWT Token 过期时间合理
- [ ] 所有用户接口需要认证
- [ ] 输入参数验证正常工作

### 8.3 性能检查

- [ ] 数据库查询优化
- [ ] 响应时间 < 200ms（获取用户信息）
- [ ] 并发处理正常

---

## 9. 未来扩展方向

1. **用户角色权限系统**：添加角色（Role）和权限（Permission）
2. **OAuth2 集成**：支持第三方登录（微信、企业微信等）
3. **审计日志**：记录用户操作历史
4. **多因素认证**：添加 2FA 支持
5. **会话管理**：实现 Token 黑名单（Redis）
6. **用户偏好设置**：主题、语言等个性化配置

---

## 10. 参考资料

- [Spring Security 官方文档](https://docs.spring.io/spring-security/reference/)
- [JWT.io](https://jwt.io/)
- [Spring Boot Validation](https://docs.spring.io/spring-boot/docs/current/reference/html/web.html#web.servlet.spring-mvc)

---

*文档版本: 1.0*
*创建日期: 2026-03-10*
*最后更新: 2026-03-10*
