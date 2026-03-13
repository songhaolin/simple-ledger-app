package com.ledger.controller;

import com.ledger.model.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 更新账单测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionUpdateControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.ledger.repository.UserRepository userRepository;

    @Autowired
    private com.ledger.repository.TransactionRepository transactionRepository;

    @Autowired
    private com.ledger.util.JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    /**
     * 测试1：正常更新账单
     */
    @Test
    public void shouldUpdateTransactionSuccessfully() throws Exception {
        // 1. 创建用户和账单
        String phone = "13800138000";
        String password = "Password123!";
        var user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        var ledger = new com.ledger.model.Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new java.util.Date());
        ledger.setUpdatedAt(new java.util.Date());
        var members = new java.util.ArrayList<com.ledger.model.Ledger.Member>();
        var member = new com.ledger.model.Ledger.Member();
        member.setUserId(user.getId());
        member.setRole("owner");
        member.setJoinedAt(new java.util.Date());
        members.add(member);
        ledger.setMembers(members);

        var ledgerRepo = (com.ledger.repository.LedgerRepository)
            com.ledger.util.ApplicationContextProvider
                .getApplicationContext()
                .getBean(com.ledger.repository.LedgerRepository.class);
        ledgerRepo.save(ledger);

        // 2. 创建账单
        var tx = new Transaction();
        tx.setLedgerId(ledger.getId());
        tx.setUserId(user.getId());
        tx.setType("expense");
        tx.setAmount(35.00);
        tx.setCategoryId("cat_001");
        tx.setCategoryName("餐饮");
        tx.setSubcategory("午餐");
        tx.setDate(new java.util.Date(System.currentTimeMillis() - 86400000L)); // 昨天
        tx.setNote("公司楼下");
        tx.setCreatedAt(new java.util.Date());
        tx.setUpdatedAt(new java.util.Date());
        tx.setIsDeleted(false);
        tx = transactionRepository.save(tx);

        // 3. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        // 4. 更新账单
        mockMvc.perform(put("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "amount": 50.00,
    "categoryId": "cat_002",
    "categoryName": "交通",
    "subcategory": "地铁",
    "note": "今天坐地铁"
}
"""))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.amount").value(50.00))
                .andExpect(jsonPath("$.data.categoryId").value("cat_002"))
                .andExpect(jsonPath("$.data.note").value("今天坐地铁"));
    }

    /**
     * 测试2：Token无效
     */
    @Test
    public void shouldFailWithInvalidToken() throws Exception {
        String txId = "tx_001";

        mockMvc.perform(put("/transactions/" + txId)
                        .header("Authorization", "Bearer invalid-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "amount": 50.00,
    "categoryId": "cat_002"
}
"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试3：账单不存在
     */
    @Test
    public void shouldFailWithTransactionNotFound() throws Exception {
        String phone = "13800138000";
        String password = "Password123!";
        
        // 创建用户
        var user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        mockMvc.perform(put("/transactions/nonexistent-id")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "amount": 50.00
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试4：金额为负数
     */
    @Test
    public void shouldFailWithNegativeAmount() throws Exception {
        // 1. 创建用户和账单
        String phone = "13800138000";
        String password = "Password123!";
        var user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        user = userRepository.save(user);

        var ledger = new com.ledger.model.Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new java.util.Date());
        ledger.setUpdatedAt(new java.util.Date());
        var members = new java.util.ArrayList<com.ledger.model.Ledger.Member>();
        var member = new com.ledger.model.Ledger.Member();
        member.setUserId(user.getId());
        member.setRole("owner");
        member.setJoinedAt(new java.util.Date());
        members.add(member);
        ledger.setMembers(members);

        var ledgerRepo = (com.ledger.repository.LedgerRepository)
            com.ledger.util.ApplicationContextProvider
                .getApplicationContext()
                .getBean(com.ledger.repository.LedgerRepository.class);
        ledgerRepo.save(ledger);

        var tx = new Transaction();
        tx.setLedgerId(ledger.getId());
        tx.setUserId(user.getId());
        tx.setType("expense");
        tx.setAmount(35.00);
        tx.setCategoryId("cat_001");
        tx.setCategoryName("餐饮");
        tx.setSubcategory("午餐");
        tx.setDate(new java.util.Date(System.currentTimeMillis() - 86400000L)); // 昨天
        tx.setNote("公司楼下");
        tx.setCreatedAt(new java.util.Date());
        tx.setUpdatedAt(new java.util.Date());
        tx.setIsDeleted(false);
        tx = transactionRepository.save(tx);

        // 2. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        // 3. 尝试更新为负数金额
        mockMvc.perform(put("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
{
    "amount": -50.00,
    "categoryId": "cat_002"
}
"""))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
