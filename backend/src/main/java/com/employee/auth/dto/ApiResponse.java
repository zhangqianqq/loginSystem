package com.employee.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * 统一 API 响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 是否成功
     */
    private boolean success;

    /**
     * 响应数据
     */
    private T data;

    /**
     * 错误信息
     */
    private ErrorResponse error;

    /**
     * 响应代码（200表示成功）
     */
    @Builder.Default
    private int code = 200;

    /**
     * 响应消息
     */
    @Builder.Default
    private String message = "success";

    /**
     * 错误信息内部类
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String code;
        private String message;
        /**
         * 额外错误详情（如字段验证错误）
         */
        private Map<String, String> details;
    }

    /**
     * 创建成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .data(data)
                .code(200)
                .message("success")
                .build();
    }

    /**
     * 创建失败响应
     */
    public static <T> ApiResponse<T> error(String code, String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .build())
                .build();
    }

    /**
     * 创建带详情的失败响应
     */
    public static <T> ApiResponse<T> error(String code, String message, Map<String, String> details) {
        return ApiResponse.<T>builder()
                .success(false)
                .error(ErrorResponse.builder()
                        .code(code)
                        .message(message)
                        .details(details)
                        .build())
                .build();
    }
}
