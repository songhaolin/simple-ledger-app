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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 删除账单测试 - TDD
 *
 * 测试目标：DELETE /api/v1/transactions/{transactionId}
 * 场景：
 * 1. 正常删除账单（软删除）
 * 2. Token无效（401）
 * 3. 账单不存在（404）
 * 4. 无权删除（非创建者）
 */
@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/ledger_test",
    "jwt.secret=my-super-secret-key-for-testing-purposes-only-please-use-a-longer-key-in-production-environment-at-least-512-bits-long-here-is-more-text-to-make-it-long-enough-for-hs512-algorithm-to-work-correctly",
    "jwt.expiration=604800000"
})
@AutoConfigureMockMvc
public class TransactionDeleteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private com.ledger.repository.UserRepository userRepository;

    @Autowired
    private com.ledger.repository.TransactionRepository transactionRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private com.ledger.util.JwtUtil jwtUtil;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
    }

    /**
     * 测试1：正常删除账单（软删除）
     */
    @Test
    public void shouldDeleteTransactionSuccessfully() throws Exception {
        // 1. 创建用户和账单
        String phone = "13800138000";
        String password = "Password123!";
        var user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(passwordEncoder.encode(password));
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

        // 4. 删除账单
        mockMvc.perform(delete("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 5. 验证软删除
        var deletedTx = transactionRepository.findById(tx.getId()).orElse(null);
        assert deletedTx != null : "账单不应该被物理删除";
        assert deletedTx.getIsDeleted() : "账单应该被软删除";
    }

    /**
     * 测试2：Token无效
     */
    @Test
    public void shouldFailWithInvalidToken() throws Exception {
        String txId = "tx_001";

        mockMvc.perform(delete("/transactions/" + txId)
                        .header("Authorization", "Bearer invalid-token"))
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
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new java.util.Date());
        user.setUpdatedAt(new java.util.Date());
        user.setIsActive(true);
        userRepository.save(user);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        mockMvc.perform(delete("/transactions/nonexistent-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试4：无权删除（非创建者）
     */
    @Test
    public void shouldFailWithNoPermission() throws Exception {
        // 1. 创建用户1（账单创建者）
        var user1 = new com.ledger.model.User();
        user1.setPhone("13800138001");
        user1.setPasswordHash(passwordEncoder.encode("Password123!"));
        user1.setNickname("张三");
        user1.setCreatedAt(new java.util.Date());
        user1.setUpdatedAt(new java.util.Date());
        user1.setIsActive(true);
        userRepository.save(user1);

        // 2. 创建用户2（尝试删除）
        var user2 = new com.ledger.model.User();
        user2.setPhone("13800138002");
        user2.setPasswordHash(passwordEncoder.encode("Password123!"));
        user2.setNickname("李四");
        user2.setCreatedAt(new java.util.Date());
        user2.setUpdatedAt(new java.util.Date());
        user2.setIsActive(true);
        userRepository.save(user2);

        // 3. 创建账单（属于用户1）
        var ledger = new com.ledger.model.Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(user1.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new java.util.Date());
        ledger.setUpdatedAt(new java.util.Date());
        var members = new java.util.ArrayList<com.ledger.model.Ledger.Member>();
        var member = new com.ledger.model.Ledger.Member();
        member.setUserId(user1.getId());
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
        tx.setUserId(user1.getId());
        tx.setType("expense");
        tx.setAmount(35.00);
        tx.setCategoryId("cat_001");
        tx.setCategoryName("餐饮");
        tx.setSubcategory("午餐");
        tx.setDate(new java.util.Date());
        tx.setNote("测试账单");
        tx.setCreatedAt(new java.util.Date());
        tx.setUpdatedAt(new java.util.Date());
        tx.setIsDeleted(false);
        tx = transactionRepository.save(tx);

        // 4. 用户2尝试删除（使用用户2的Token）
        String token = jwtUtil.generateToken(user2.getId(), user2.getPhone());

        mockMvc.perform(delete("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
