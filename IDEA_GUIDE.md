# IntelliJ IDEA 使用指南

## 📂 打开项目

### 方式1：从文件夹打开
1. 打开 IntelliJ IDEA
2. 点击 **File** → **Open**
3. 导航到 `k:\Cursor\loginSystem\backend`
4. 选择该文件夹并点击 **OK**

### 方式2：从资源管理器
1. 打开文件夹 `k:\Cursor\loginSystem\backend`
2. 右键点击 `pom.xml` 文件
3. 选择 **Open with** → **IntelliJ IDEA**

### 方式3：拖拽方式
1. 打开 IntelliJ IDEA
2. 直接将 `k:\Cursor\loginSystem\backend` 文件夹拖到 IDEA 窗口中

---

## ⚙️ 项目配置

### Maven 配置

IDEA 会自动识别 `pom.xml` 并导入 Maven 项目。

**手动配置 Maven：**
1. **File** → **Settings** (或 Ctrl+Alt+S)
2. 导航到 **Build, Execution, Deployment** → **Build Tools** → **Maven**
3. 设置 Maven home directory：
   ```
   D:\WorkTool\apache-maven-3.6.0
   ```
   或你的 Maven 安装路径

### JDK 配置

1. **File** → **Project Structure** (Ctrl+Alt+Shift+S)
2. 在 **Project** 下设置 **SDK** 为 **JDK 17**
3. 在 **Modules** 下确认语言级别为 **17**

---

## 🚀 运行项目

### 方式1：运行启动类

1. 打开 `src/main/java/com/employee/auth/AuthApplication.java`
2. 点击类名左侧的绿色三角形 ▶️
3. 选择 **Run 'AuthApplication'**

### 方式2：使用 Maven 运行

1. 打开右侧 **Maven** 面板
2. 展开 **employee-auth** → **Plugins**
3. 双击 **spring-boot** → **spring-boot:run**

### 方式3：使用 Run Configuration

1. 点击右上角 **Edit Configurations...**
2. 点击 **+** → **Spring Boot**
3. 配置如下：
   - Name: `AuthApplication`
   - Main class: `com.employee.auth.AuthApplication`
   - Module: `employee-auth`
4. 点击 **OK**
5. 点击绿色运行按钮 ▶️

---

## 📝 运行后操作

### 1. 查看控制台输出

运行后，控制台会显示启动日志：

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.2)

2026-03-10 18:00:00.000  INFO --- Starting AuthApplication...
```

### 2. 等待依赖下载

首次运行，Maven 会下载所有依赖包（可能需要几分钟）

### 3. 启动成功标志

看到以下信息表示启动成功：

```
Started AuthApplication in 5.234 seconds
```

---

## 🧪 测试 API

### 使用 IDEA 内置 HTTP Client

1. 在项目中创建 `test-api.http` 文件
2. 输入以下内容：

```http
### 测试登录
POST http://localhost:8080/api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin123"
}
```

3. 点击请求旁边的 ▶️ 按钮执行

### 或使用 Postman/curl

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

---

## 🔧 常见问题

### 1. Maven 依赖下载慢

**解决方案：** 配置阿里云镜像

打开 `pom.xml` 添加：

```xml
<repositories>
    <repository>
        <id>aliyun</id>
        <url>https://maven.aliyun.com/repository/public</url>
    </repository>
</repositories>
```

### 2. 找不到 JDK 17

**解决方案：**
- 下载并安装 JDK 17
- 在 IDEA 中配置 JDK 路径

### 3. 端口 8080 被占用

**解决方案：** 修改 `application.yml`

```yaml
server:
  port: 8081
```

### 4. 数据库连接失败

**检查项：**
- [ ] MySQL 服务是否启动
- [ ] 用户名密码是否正确
- [ ] 数据库 `employee_system` 是否已创建
- [ ] 防火墙是否允许 3306 端口

---

## 📚 项目结构预览

IDEA 中打开后，项目结构如下：

```
employee-auth
├── .idea
├── src
│   └── main
│       ├── java/com/employee/auth
│       │   ├── config          # 配置类
│       │   ├── controller      # 控制器
│       │   ├── dto             # 数据传输对象
│       │   ├── entity          # 实体类
│       │   ├── repository      # 数据访问层
│       │   ├── security        # 安全类
│       │   ├── service         # 业务逻辑层
│       │   └── AuthApplication.java
│       └── resources
│           └── application.yml
└── pom.xml
```

---

## 🎯 下一步

1. ✅ 打开项目
2. ✅ 等待 Maven 索引完成
3. ✅ 配置 JDK 17
4. ⏳ 运行 `AuthApplication`
5. ⏳ 测试登录接口
6. ⏳ 启动前端测试完整流程

---

*更新时间：2026-03-10*
