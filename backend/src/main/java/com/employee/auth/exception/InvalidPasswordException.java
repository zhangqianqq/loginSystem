package com.employee.auth.exception;

/**
 * 密码错误异常
 * 当当前密码验证失败时抛出
 */
public class InvalidPasswordException extends RuntimeException {

    /**
     * 构造函数
     * @param message 错误消息
     */
    public InvalidPasswordException(String message) {
        super(message);
    }

    /**
     * 默认构造函数
     */
    public InvalidPasswordException() {
        super("当前密码不正确");
    }
}
