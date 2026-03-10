package com.ledger.controller;

import com.ledger.model.User;
import com.ledger.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Response<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getPhone(),
                request.getPassword(),
                request.getNickname()
        );

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());
        String refreshToken = jwtUtil.generateRefreshToken(user.getId());

        Map<String, Object> data = new HashMap<>();
        data.put("userId", user.getId());
        data.put("token", token);
        data.put("refreshToken", refreshToken);

        return Response.success(data);
    }

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Response<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(
                request.getPhone(),
                request.getPassword()
        );

        return Response.success(data);
    }

    /**
     * 注册请求体
     */
    @lombok.Data
    public static class RegisterRequest {
        private String phone;
        private String password;
        private String nickname;
    }

    /**
     * 登录请求体
     */
    @lombok.Data
    public static class LoginRequest {
        private String phone;
        private String password;
    }
}
