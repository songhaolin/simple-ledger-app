package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 创建账单测试 - TDD 示例
 * 
 * 测试目标：POST /api/v1/transactions
 * 场景：
 * 1. 正常创建账单
 * 2. Token无效
 * 3. 缺少必填字段
 * 4. 金额为负数
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    /**
     * 测试1：正常创建账单
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - 返回 transactionId
     */
    @Test
    public void shouldCreateTransactionSuccessfully() throws Exception {
        // 先注册用户并生成Token
        String phone = "13800138000";
        String password = "Password123!";
        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
{
    "phone": "%s",
    "password": "%s"
}
""", phone, password)));

        // 获取账本ID
        String ledgerId = "ledger_001";

        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(String.format("""
{
    "ledgerId": "%s",
    "type": "expense",
    "amount": 35.00,
    "categoryId": "cat_001",
    "categoryName": "餐饮",
    "subcategory": "午餐",
    "date": "%s",
    "note": "公司楼下"
}
""", ledgerId, new Date().toString())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.transactionId").exists());
    }

    /**
     * 测试2：Token无效
     * 
     * 预期：
     * - 状态码 401
     * - 返回错误信息
     */
    @Test
    public void shouldFailWithInvalidToken() throws Exception {
        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "ledgerId": "ledger_001",
    "type": "expense",
    "amount": 35.00,
    "categoryId": "cat_001",
    "categoryName": "餐饮"
}
"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试3：缺少必填字段
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误码 4001
     */
    @Test
    public void shouldFailWithMissingFields() throws Exception {
        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "type": "expense",
    "amount": 35.00
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.code").value("4001"));
    }

    /**
     * 测试4：金额为负数
     * 
     * 预期：
     * - 状态码 400
     * - 返回错误信息
     */
    @Test
    public void shouldFailWithNegativeAmount() throws Exception {
        mockMvc.perform(post("/transactions")
                        .header("Authorization", "Bearer test-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "ledgerId": "ledger_001",
    "type": "expense",
    "amount": -35.00,
    "categoryId": "cat_001",
    "categoryName": "餐饮"
}
"""))
                .andExpect(status().isBadRequest());
    }
}
