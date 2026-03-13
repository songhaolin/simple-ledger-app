package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 收支统计测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private com.ledger.util.JwtUtil jwtUtil;

    private String testLedgerId;
    private String testUserId;

    @BeforeEach
    public void setup() {
        userRepository.deleteAll();
        transactionRepository.deleteAll();
        testLedgerId = null;
        testUserId = null;
    }

    /**
     * 测试1：正常获取收支统计
     */
    @Test
    public void shouldGetStatisticsSummary() throws Exception {
        // 1. 创建测试数据
        String token = createTestData();

        // 2. 调用统计接口
        mockMvc.perform(get("/statistics/summary?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalIncome").exists())
                .andExpect(jsonPath("$.data.totalExpense").exists())
                .andExpect(jsonPath("$.data.balance").exists())
                .andExpect(jsonPath("$.data.categorySummary").isArray())
                .andExpect(jsonPath("$.data.dailyTrend").isArray());
    }

    /**
     * 测试2：指定日期范围查询
     */
    @Test
    public void shouldGetStatisticsWithDateRange() throws Exception {
        String token = createTestData();

        mockMvc.perform(get("/statistics/summary?ledgerId=" + testLedgerId + "&startDate=2026-03-01&endDate=2026-03-10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试3：无Token访问
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/statistics/summary?ledgerId=test-ledger")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试4：缺少ledgerId参数
     */
    @Test
    public void shouldFailWithoutLedgerId() throws Exception {
        String token = jwtUtil.generateToken(java.util.UUID.randomUUID().toString(), "13800138000");
        mockMvc.perform(get("/statistics/summary")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试5：空账本数据
     */
    @Test
    public void shouldReturnZeroStatistics() throws Exception {
        // 创建用户但没有账单数据
        String token = createUserWithoutData();

        mockMvc.perform(get("/statistics/summary?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalIncome").value(0.0))
                .andExpect(jsonPath("$.data.totalExpense").value(0.0))
                .andExpect(jsonPath("$.data.balance").value(0.0));
    }

    /**
     * 创建测试数据
     */
    private String createTestData() {
        // 创建用户
        String phone = "13800138000";
        String password = "Password123!";
        com.ledger.model.User user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("张三");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);
        user = userRepository.save(user);
        testUserId = user.getId();

        // 创建账本
        com.ledger.model.Ledger ledger = new com.ledger.model.Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new Date());
        ledger.setUpdatedAt(new Date());

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
        testLedgerId = ledger.getId();

        // 创建收入账单
        Transaction income1 = new Transaction();
        income1.setLedgerId(ledger.getId());
        income1.setUserId(user.getId());
        income1.setType("income");
        income1.setAmount(5000.00);
        income1.setCategoryId("cat_101");
        income1.setCategoryName("工资");
        income1.setSubcategory("月工资");
        income1.setDate(new Date(System.currentTimeMillis() - 86400000L * 5)); // 5天前
        income1.setNote("三月份工资");
        income1.setCreatedAt(new Date());
        income1.setUpdatedAt(new Date());
        income1.setIsDeleted(false);
        transactionRepository.save(income1);

        // 创建支出账单（餐饮）
        Transaction expense1 = new Transaction();
        expense1.setLedgerId(ledger.getId());
        expense1.setUserId(user.getId());
        expense1.setType("expense");
        expense1.setAmount(1200.00);
        expense1.setCategoryId("cat_001");
        expense1.setCategoryName("餐饮");
        expense1.setSubcategory("午餐");
        expense1.setDate(new Date(System.currentTimeMillis() - 86400000L * 3)); // 3天前
        expense1.setNote("公司食堂");
        expense1.setCreatedAt(new Date());
        expense1.setUpdatedAt(new Date());
        expense1.setIsDeleted(false);
        transactionRepository.save(expense1);

        Transaction expense2 = new Transaction();
        expense2.setLedgerId(ledger.getId());
        expense2.setUserId(user.getId());
        expense2.setType("expense");
        expense2.setAmount(800.00);
        expense2.setCategoryId("cat_001");
        expense2.setCategoryName("餐饮");
        expense2.setSubcategory("晚餐");
        expense2.setDate(new Date(System.currentTimeMillis() - 86400000L * 1)); // 1天前
        expense2.setNote("外卖");
        expense2.setCreatedAt(new Date());
        expense2.setUpdatedAt(new Date());
        expense2.setIsDeleted(false);
        transactionRepository.save(expense2);

        // 创建支出账单（交通）
        Transaction expense3 = new Transaction();
        expense3.setLedgerId(ledger.getId());
        expense3.setUserId(user.getId());
        expense3.setType("expense");
        expense3.setAmount(500.00);
        expense3.setCategoryId("cat_002");
        expense3.setCategoryName("交通");
        expense3.setSubcategory("地铁");
        expense3.setDate(new Date(System.currentTimeMillis() - 86400000L * 2)); // 2天前
        expense3.setNote("上班通勤");
        expense3.setCreatedAt(new Date());
        expense3.setUpdatedAt(new Date());
        expense3.setIsDeleted(false);
        transactionRepository.save(expense3);

        // 创建支出账单（购物）
        Transaction expense4 = new Transaction();
        expense4.setLedgerId(ledger.getId());
        expense4.setUserId(user.getId());
        expense4.setType("expense");
        expense4.setAmount(500.00);
        expense4.setCategoryId("cat_003");
        expense4.setCategoryName("购物");
        expense4.setSubcategory("日用品");
        expense4.setDate(new Date(System.currentTimeMillis() - 86400000L * 4)); // 4天前
        expense4.setNote("超市购物");
        expense4.setCreatedAt(new Date());
        expense4.setUpdatedAt(new Date());
        expense4.setIsDeleted(false);
        transactionRepository.save(expense4);

        // 生成Token
        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }

    /**
     * 创建用户但没有账单数据
     */
    private String createUserWithoutData() {
        String phone = "13800138999";
        String password = "Password123!";
        com.ledger.model.User user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname("李四");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);
        user = userRepository.save(user);
        testUserId = user.getId();

        // 创建空账本
        com.ledger.model.Ledger ledger = new com.ledger.model.Ledger();
        ledger.setName("空账本");
        ledger.setType("personal");
        ledger.setOwnerId(user.getId());
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new Date());
        ledger.setUpdatedAt(new Date());

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
        testLedgerId = ledger.getId();

        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }
}
