package com.ledger.controller;

import com.ledger.exception.BusinessException;
import com.ledger.model.Ledger;
import com.ledger.model.User;
import com.ledger.repository.LedgerRepository;
import com.ledger.repository.UserRepository;
import com.ledger.util.JwtUtil;
import com.ledger.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 账本控制器测试 - TDD 示例
 *
 * 测试目标：GET /api/v1/ledgers
 * 场景：
 * 1. 获取用户所有账本（正常）
 * 2. 无Token访问（401）
 * 3. Token无效（401）
 */
@SpringBootTest
@AutoConfigureMockMvc
public class LedgerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        ledgerRepository.deleteAll();
    }

    /**
     * 测试1：正常获取账本列表
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - 返回用户的所有账本
     */
    @Test
    public void shouldReturnAllUserLedgers() throws Exception {
        // 先注册一个用户
        String phone = "13800138000";
        String password = "Password123!";
        
        User user = new User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        // 创建账本
        Ledger ledger = new Ledger();
        ledger.setName("我的账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new java.util.Date());
        ledger.setUpdatedAt(new java.util.Date());
        ledgerRepository.save(ledger);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        mockMvc.perform(get("/ledgers")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").value("我的账本"))
                .andExpect(jsonPath("$.data[0].type").value("personal"));
    }

    /**
     * 测试2：无Token访问（401）
     * 
     * 预期：
     * - 状态码 401
     * - 返回错误信息
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/ledgers")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试3：Token无效（401）
     * 
     * 预期：
     * - 状态码 401
     * - 返回错误信息
     */
    @Test
    public void shouldFailWithInvalidToken() throws Exception {
        mockMvc.perform(get("/ledgers")
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }
}
