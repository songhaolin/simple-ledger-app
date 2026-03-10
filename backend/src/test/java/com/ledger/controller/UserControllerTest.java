package com.ledger.controller;

import com.ledger.model.User;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户注册测试 - TDD 示例
 * 
 * 测试目标：POST /api/v1/users/register
 * 场景：
 * 1. 正常注册
 * 2. 手机号格式错误
 * 3. 手机号已注册
 * 4. 密码格式错误
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
     * 测试1：正常注册
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - 返回 userId, token, refreshToken
     */
    @Test
    public void shouldRegisterSuccessfully() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000",
    "password": "Password123!",
    "nickname": "张三"
}
"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.userId").exists())
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    /**
     * 测试2：手机号格式错误
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 1001
     */
    @Test
    public void shouldFailWithInvalidPhone() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "123",
    "password": "Password123!",
    "nickname": "张三"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("1001"))
                .andExpect(jsonPath("$.error.message").value("手机号格式错误"));
    }

    /**
     * 测试3：手机号已注册
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 1003
     */
    @Test
    public void shouldFailWithDuplicatePhone() throws Exception {
        // 先注册一个用户
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000",
    "password": "Password123!",
    "nickname": "张三"
}
"""))
                .andExpect(status().isOk());

        // 再次注册相同手机号
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000",
    "password": "Password456!",
    "nickname": "李四"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("1003"))
                .andExpect(jsonPath("$.error.message").value("手机号已注册"));
    }

    /**
     * 测试4：密码格式错误
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 1002
     */
    @Test
    public void shouldFailWithInvalidPassword() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13900139000",
    "password": "123",
    "nickname": "王五"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("1002"))
                .andExpect(jsonPath("$.error.message").value("密码格式错误"));
    }

    /**
     * 测试5：缺少必填字段
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 4001
     */
    @Test
    public void shouldFailWithMissingFields() throws Exception {
        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "phone": "13800138000"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("4001"));
    }
}
