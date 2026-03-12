package com.employee.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 重置密码请求 DTO
 * 用于接收重置密码请求参数
 */
@Data
public class ResetPasswordRequest {

    /**
     * 重置 Token
     */
    @NotBlank(message = "Token不能为空")
    private String token;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 8, message = "密码至少需要8个字符")
    private String newPassword;
}
