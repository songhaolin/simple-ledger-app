package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.model.User;
import com.ledger.repository.UserRepository;
import com.ledger.util.JwtUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册
     */
    public User register(String phone, String password, String nickname) {
        // 1. 验证手机号
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PHONE,
                    "手机号不能为空"
            );
        }

        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PHONE,
                    "手机号格式不正确"
            );
        }

        // 2. 检查手机号是否已存在
        User existingUser = userRepository.findByPhone(phone);
        if (existingUser != null) {
            throw new BusinessException(
                    ErrorCodes.PHONE_EXISTS,
                    "手机号已被注册"
            );
        }

        // 3. 创建用户
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : phone);
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);

        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String phone, String password) {
        // 1. 验证手机号
        if (phone == null || phone.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PHONE,
                    "手机号不能为空"
            );
        }

        // 1.1 验证密码
        if (password == null || password.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "密码不能为空"
            );
        }

        // 2. 查找用户
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new BusinessException(
                    ErrorCodes.WRONG_PASSWORD,
                    "手机号或密码错误"
            );
        }

        // 3. 验证密码
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(
                    ErrorCodes.WRONG_PASSWORD,
                    "手机号或密码错误"
            );
        }

        // 4. 更新最后登录时间
        user.setLastLoginAt(new Date());
        userRepository.save(user);

        // 5. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 6. 返回用户信息和Token
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", token);
        data.put("refreshToken", refreshToken);

        // 添加用户信息（不包括敏感字段）
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("nickname", user.getNickname());
        userInfo.put("phone", user.getPhone());
        data.put("user", userInfo);

        return data;
    }
}
