package com.employee.auth.util;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 临时测试接口 - 生产环境请删除
 */
@RestController
@RequestMapping("/test")
public class TestController {

    /**
     * 生成 admin123 的 BCrypt 密码哈希
     */
    @GetMapping("/password-hash")
    public String generatePasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("admin123");

        return "<h2>密码哈希生成工具</h2>" +
               "<p><strong>明文:</strong> admin123</p>" +
               "<p><strong>BCrypt Hash ($2a$10$...):</strong></p>" +
               "<p style='font-family: monospace; font-size: 14px;'>" + hash + "</p>" +
               "<br><hr>" +
               "<p><strong>MySQL 更新语句：</strong></p>" +
               "<pre style='background: #f5f5f5; padding: 15px; border-radius: 8px;'>UPDATE users\nSET password = '" + hash + "'\nWHERE username = 'admin';</pre>";
    }
}
