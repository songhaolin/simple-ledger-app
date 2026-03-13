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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 预算管理测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class BudgetControllerTest {

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
        com.ledger.repository.BudgetRepository budgetRepo = (com.ledger.repository.BudgetRepository)
            com.ledger.util.ApplicationContextProvider
                .getApplicationContext()
                .getBean(com.ledger.repository.BudgetRepository.class);
        budgetRepo.deleteAll();
        testLedgerId = null;
        testUserId = null;
    }

    /**
     * 测试1：设置分类预算
     */
    @Test
    public void shouldSetBudget() throws Exception {
        String token = createTestData();

        String requestBody = "{\"ledgerId\": \"" + testLedgerId + "\", \"budgets\": [{\"categoryId\": \"cat_001\", \"amount\": 1000.00}, {\"categoryId\": \"cat_002\", \"amount\": 500.00}]}";

        mockMvc.perform(post("/budgets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 测试2：获取预算统计
     */
    @Test
    public void shouldGetBudgetStatistics() throws Exception {
        String token = createTestData();

        mockMvc.perform(get("/budgets/statistics?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalBudget").exists())
                .andExpect(jsonPath("$.data.totalExpense").exists())
                .andExpect(jsonPath("$.data.remainingBudget").exists())
                .andExpect(jsonPath("$.data.categoryBudgets").isArray());
    }

    /**
     * 测试3：预算超支提醒
     */
    @Test
    public void shouldAlertOverBudget() throws Exception {
        String token = createTestData();

        mockMvc.perform(get("/budgets/statistics?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.overBudgetCategories").isArray());
    }

    /**
     * 测试4：无Token访问
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(post("/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "ledgerId": "test-ledger",
                      "budgets": [
                        {
                          "categoryId": "cat_001",
                          "amount": 1000.00
                        }
                      ]
                    }
                """))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试4：更新分类预算
     */
    @Test
    public void shouldUpdateBudget() throws Exception {
        String token = createTestData();

        // 先设置预算
        mockMvc.perform(post("/budgets")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "ledgerId": " ledgerId,
                      "budgets": [
                        {
                          "categoryId": "cat_001",
                          "amount": 1000.00
                        }
                      ]
                    }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

        // 然后更新预算（使用相同的testLedgerId，不重新调用createTestData())
        mockMvc.perform(put("/budgets/cat_001?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"amount\": 1500.00}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").exists());
    }

    /**
     * 创建测试数据
     */
    private String createTestData() {
        return createTestData("张三", "13800138000", "Password123!");
    }

    private String createTestData(String nickname, String phone, String password) {
        // 创建用户
        com.ledger.model.User user = new com.ledger.model.User();
        user.setPhone(phone);
        user.setPasswordHash(new BCryptPasswordEncoder().encode(password));
        user.setNickname(nickname);
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

        // 创建支出账单（餐饮1200元，但预算1000元，会超支）
        Transaction expense1 = new Transaction();
        expense1.setLedgerId(ledger.getId());
        expense1.setUserId(user.getId());
        expense1.setType("expense");
        expense1.setAmount(1200.00);
        expense1.setCategoryId("cat_001");
        expense1.setCategoryName("餐饮");
        expense1.setSubcategory("午餐");
        expense1.setDate(new Date(System.currentTimeMillis() - 86400000L * 3));
        expense1.setNote("公司食堂");
        expense1.setCreatedAt(new Date());
        expense1.setUpdatedAt(new Date());
        expense1.setIsDeleted(false);
        transactionRepository.save(expense1);

        // 创建支出账单（交通400元，预算500元，未超支）
        Transaction expense2 = new Transaction();
        expense2.setLedgerId(ledger.getId());
        expense2.setUserId(user.getId());
        expense2.setType("expense");
        expense2.setAmount(400.00);
        expense2.setCategoryId("cat_002");
        expense2.setCategoryName("交通");
        expense2.setSubcategory("地铁");
        expense2.setDate(new Date(System.currentTimeMillis() - 86400000L * 2));
        expense2.setNote("上班通勤");
        expense2.setCreatedAt(new Date());
        expense2.setUpdatedAt(new Date());
        expense2.setIsDeleted(false);
        transactionRepository.save(expense2);

        // 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        return token;
    }
}
