package com.ledger.controller;

import com.ledger.model.User;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户登录测试 - TDD 示例
 *
 * 测试目标：POST /api/v1/users/login
 * 场景：
 * 1. 正常登录
 * 2. 用户不存在
 * 3. 密码错误
 * 4. 缺少参数
 */
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
    }

    /**
     * 测试1：正常登录
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - 返回 userId, token, refreshToken, user信息
     */
    @Test
    public void shouldLoginSuccessfully() throws Exception {
        // 先注册一个用户
        String phone = "13800138000";
        String password = "Password123!";
        
        // 直接插入用户（模拟注册）
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
{
    "phone": "%s",
    "password": "%s"
}
""", phone, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andExpect(jsonPath("$.data.user.nickname").value("张三"));
    }

    /**
     * 测试2：用户不存在
     * 
     * 预期：
     * - 状态码 401
     * - 返回错误码 1004
     */
    @Test
    public void shouldFailWithUserNotFound() throws Exception {
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13900139000",
    "password": "Password123!"
}
"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("1004"))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));
    }

    /**
     * 测试3：密码错误
     * 
     * 预期：
     * - 状态码 401
     * - 返回错误码 1004
     */
    @Test
    public void shouldFailWithWrongPassword() throws Exception {
        // 先注册一个用户
        String phone = "13800138000";
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode("CorrectPassword123!"));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        // 使用错误密码登录
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000",
    "password": "WrongPassword123!"
}
"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("1004"))
                .andExpect(jsonPath("$.message").value("手机号或密码错误"));
    }

    /**
     * 测试4：缺少参数
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 4001
     */
    @Test
    public void shouldFailWithMissingFields() throws Exception {
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("4001"));
    }
}
