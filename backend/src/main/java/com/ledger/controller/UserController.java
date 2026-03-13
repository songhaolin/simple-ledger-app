package com.ledger.controller;

import com.ledger.model.User;
import com.ledger.service.UserService;
import com.ledger.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@Tag(name = "用户管理", description = "用户注册、登录相关接口")
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    /**
     * 用户注册
     */
    @Operation(summary = "用户注册", description = "使用手机号和密码注册新用户")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "注册成功",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Map<String, Object>>> register(@Valid @RequestBody RegisterRequest request) {
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

        return ApiResponse.success(data);
    }

    /**
     * 用户登录
     */
    @Operation(summary = "用户登录", description = "使用手机号和密码登录，返回JWT Token")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(schema = @Schema(implementation = ApiResponse.class))
            )
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Map<String, Object>>> login(@RequestBody LoginRequest request) {
        Map<String, Object> data = userService.login(
                request.getPhone(),
                request.getPassword()
        );

        return ApiResponse.success(data);
    }

    /**
     * 注册请求体
     */
    @Schema(description = "用户注册请求")
    public static class RegisterRequest {
        @NotBlank(message = "手机号不能为空")
        @Schema(description = "手机号", example = "13800138000", required = true)
        private String phone;

        @NotBlank(message = "密码不能为空")
        @Schema(description = "密码", example = "123456", required = true)
        private String password;

        @Schema(description = "昵称（可选）", example = "小明")
        private String nickname;

        public String getPhone() {
            return phone;
        }

        public String getPassword() {
            return password;
        }

        public String getNickname() {
            return nickname;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }
    }

    /**
     * 登录请求体
     */
    @Schema(description = "用户登录请求")
    public static class LoginRequest {
        @Schema(description = "手机号", example = "13800138000", required = true)
        private String phone;

        @Schema(description = "密码", example = "123456", required = true)
        private String password;

        public String getPhone() {
            return phone;
        }

        public String getPassword() {
            return password;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }
}
