package com.employee.auth.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 密码重置实体类
 * 对应数据库 password_resets 表
 */
@Entity
@Table(name = "password_resets")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordReset {

    /**
     * 重置ID（主键）
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 用户ID（外键）
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 重置Token
     */
    @Column(nullable = false, unique = true)
    private String token;

    /**
     * 过期时间
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 检查Token是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }
}
