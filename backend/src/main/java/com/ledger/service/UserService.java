package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.model.User;
import com.ledger.repository.UserRepository;
import com.ledger.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 用户注册
     */
    public User register(String phone, String password, String nickname) {
        // 1. 验证手机号格式
        if (!isValidPhone(phone)) {
            throw new BusinessException(
                BusinessException.ErrorCodes.INVALID_PHONE,
                "手机号格式错误"
            );
        }

        // 2. 验证密码格式
        if (!isValidPassword(password)) {
            throw new BusinessException(
                BusinessException.ErrorCodes.INVALID_PASSWORD,
                "密码格式错误"
            );
        }

        // 3. 检查手机号是否已注册
        if (userRepository.existsByPhone(phone)) {
            throw new BusinessException(
                BusinessException.ErrorCodes.PHONE_EXISTS,
                "手机号已注册"
            );
        }

        // 4. 创建用户
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname(nickname != null ? nickname : phone.substring(7));
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);

        return userRepository.save(user);
    }

    /**
     * 用户登录
     */
    public Map<String, Object> login(String phone, String password) {
        // 1. 查找用户
        User user = userRepository.findByPhone(phone);
        if (user == null) {
            throw new BusinessException(
                "1004",
                "用户不存在"
            );
        }

        // 2. 验证密码
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new BusinessException(
                "1004",
                "密码错误"
            );
        }

        // 3. 更新最后登录时间
        user.setLastLoginAt(new Date());
        userRepository.save(user);

        // 4. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        // 5. 返回用户信息和Token
        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", token);
        data.put("refreshToken", refreshToken);
        data.put("user", Map.of(
            "nickname", user.getNickname(),
            "avatar", user.getAvatar()
        ));

        return data;
    }

    /**
     * 验证手机号格式
     */
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^1[3-9]\\d{9}$");
    }

    /**
     * 验证密码格式（至少8位，包含字母和数字）
     */
    private boolean isValidPassword(String password) {
        return password != null
                && password.length() >= 8
                && password.matches(".*[A-Za-z].*")
                && password.matches(".*\\d.*");
    }
}
