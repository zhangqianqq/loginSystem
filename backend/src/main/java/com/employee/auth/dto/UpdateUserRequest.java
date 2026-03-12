package com.employee.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 更新用户信息请求 DTO
 * 用于用户更新个人资料
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
