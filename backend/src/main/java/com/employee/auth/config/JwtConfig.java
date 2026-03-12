package com.employee.auth.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性类
 * 从 application.yml 读取 JWT 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {

    /**
     * JWT 密钥（至少 256 位）
     */
    private String secret;

    /**
     * 访问令牌过期时间（毫秒）
     */
    private Long expiration;

    /**
     * 刷新令牌过期时间（毫秒）
     */
    private Long refreshExpiration;
}
