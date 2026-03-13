package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 获取账单列表测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionListControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private com.ledger.util.JwtUtil jwtUtil;

    private String testLedgerId;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        testLedgerId = null;
    }

    /**
     * 测试1：正常获取账单列表（第1页，10条）
     */
    @Test
    public void shouldReturnTransactionListFirstPage() throws Exception {
        // 创建测试数据
        String token = createTestDataWithToken(5);

        mockMvc.perform(get("/transactions?ledgerId=" + testLedgerId + "&page=1&limit=10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").value(5))
                .andExpect(jsonPath("$.data.currentPage").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.pageSize").value(10));
    }

    /**
     * 测试2：获取第二页
     */
    @Test
    public void shouldReturnSecondPage() throws Exception {
        String token = createTestDataWithToken(15);

        mockMvc.perform(get("/transactions?ledgerId=" + testLedgerId + "&page=2&limit=10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.currentPage").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(2))
                .andExpect(jsonPath("$.data.content[0].id").exists());
    }

    /**
     * 测试3：无Token访问
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/transactions?ledgerId=" + testLedgerId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试4：缺少ledgerId参数
     */
    @Test
    public void shouldFailWithoutLedgerId() throws Exception {
        String token = jwtUtil.generateToken(java.util.UUID.randomUUID().toString(), "13800138000");
        mockMvc.perform(get("/transactions?page=1&limit=10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试5：无效page参数
     */
    @Test
    public void shouldFailWithInvalidPageParam() throws Exception {
        String token = jwtUtil.generateToken(java.util.UUID.randomUUID().toString(), "13800138000");
        mockMvc.perform(get("/transactions?ledgerId=" + testLedgerId + "&page=abc&limit=10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试6：page超出范围
     */
    @Test
    public void shouldReturnEmptyListForPageOutOfRange() throws Exception {
        String token = createTestDataWithToken(3);

        mockMvc.perform(get("/transactions?ledgerId=" + testLedgerId + "&page=2&limit=10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    /**
     * 创建测试数据并返回token
     */
    private String createTestDataWithToken(int count) {
        // 创建用户和账本
        String phone = "13800138000";
        String password = "Password123!";
        
        // 模拟注册（绕过密码验证）
        com.ledger.model.User user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);
        user = userRepository.save(user);

        // 创建账本
        com.ledger.model.Ledger ledger = new com.ledger.model.Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new Date());
        ledger.setUpdatedAt(new Date());
        
        // 手动设置members
        java.util.ArrayList<com.ledger.model.Ledger.Member> members = new java.util.ArrayList<>();
        com.ledger.model.Ledger.Member member = new com.ledger.model.Ledger.Member();
        member.setUserId(user.getId());
        member.setRole("owner");
        member.setJoinedAt(new Date());
        members.add(member);
        ledger.setMembers(members);
        
        com.ledger.repository.LedgerRepository ledgerRepo = (com.ledger.repository.LedgerRepository)
            com.ledger.util.ApplicationContextProvider
                .getApplicationContext()
                .getBean(com.ledger.repository.LedgerRepository.class);
        ledger = ledgerRepo.save(ledger);

        // 创建账单
        for (int i = 0; i < count; i++) {
            Transaction tx = new Transaction();
            tx.setLedgerId(ledger.getId()); // 使用实际保存的ledger ID
            tx.setUserId(user.getId());
            tx.setType("expense");
            tx.setAmount(35.0 + i);
            tx.setCategoryId("cat_001");
            tx.setCategoryName("餐饮");
            tx.setSubcategory("测试");
            tx.setDate(new Date(System.currentTimeMillis() - (i * 86400000L))); // 每天一笔记账
            tx.setNote("测试账单 " + i);
            tx.setCreatedAt(new Date());
            tx.setUpdatedAt(new Date());
            tx.setIsDeleted(false);
            transactionRepository.save(tx);
        }

        // 生成并返回valid token，并保存ledgerId供后续使用
                // 保存ledgerId供测试使用
        testLedgerId = ledger.getId();

        // 生成并返回valid token
        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }
}
