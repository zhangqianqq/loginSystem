package com.employee.auth.service;

import com.employee.auth.dto.ChangePasswordRequest;
import com.employee.auth.dto.UpdateUserRequest;
import com.employee.auth.dto.UserInfoResponse;

/**
 * 用户服务接口
 * 定义用户信息查询、更新、密码修改等业务操作
 */
public interface UserService {

    /**
     * 根据用户ID获取用户信息
     * @param userId 用户ID
     * @return 用户信息响应
     */
    UserInfoResponse getUserById(Long userId);

    /**
     * 更新用户信息
     * @param userId 用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    UserInfoResponse updateUser(Long userId, UpdateUserRequest request);

    /**
     * 修改用户密码
     * @param userId 用户ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
