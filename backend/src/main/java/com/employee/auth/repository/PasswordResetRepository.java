package com.employee.auth.repository;

import com.employee.auth.entity.PasswordReset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 密码重置数据访问接口
 */
@Repository
public interface PasswordResetRepository extends JpaRepository<PasswordReset, Long> {

    /**
     * 根据Token查找重置记录
     * @param token 重置Token
     * @return 重置记录
     */
    Optional<PasswordReset> findByToken(String token);

    /**
     * 根据用户ID查找重置记录
     * @param userId 用户ID
     * @return 重置记录列表
     */
    List<PasswordReset> findByUserId(Long userId);

    /**
     * 删除过期的重置记录
     * @param now 当前时间
     */
    void deleteByExpiresAtBefore(LocalDateTime now);
}
