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
    public Response<Map<String, String>> register(@RequestBody RegisterRequest request) {
        User user = userService.register(
                request.getPhone(),
                request.getPassword(),
                request.getNickname()
        );

        // 返回用户信息（暂时不生成JWT，简化实现）
        Map<String, String> data = new HashMap<>();
        data.put("userId", user.getId());
        // TODO: 生成JWT token
        data.put("token", "temporary-token");
        data.put("refreshToken", "temporary-refresh-token");

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
}
