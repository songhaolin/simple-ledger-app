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
 * 数据导出测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ExportControllerTest {

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
     * 测试1：正常导出CSV
     */
    @Test
    public void shouldExportCsvSuccessfully() throws Exception {
        String token = createTestData();

        mockMvc.perform(get("/export/csv?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(header().string("Content-Type", "text/csv;charset=UTF-8"))
                .andExpect(content().contentTypeCompatibleWith("text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("日期,类型,金额,分类,子分类,备注")));
    }

    /**
     * 测试2：指定日期范围导出
     */
    @Test
    public void shouldExportCsvWithDateRange() throws Exception {
        String token = createTestData();

        mockMvc.perform(get("/export/csv?ledgerId=" + testLedgerId + "&startDate=2026-03-01&endDate=2026-03-10")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().contentTypeCompatibleWith("text/csv"));
    }

    /**
     * 测试3：无Token访问
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        mockMvc.perform(get("/export/csv?ledgerId=test-ledger")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试4：缺少ledgerId参数
     */
    @Test
    public void shouldFailWithoutLedgerId() throws Exception {
        String token = jwtUtil.generateToken(java.util.UUID.randomUUID().toString(), "13800138000");
        mockMvc.perform(get("/export/csv")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试5：空数据导出
     */
    @Test
    public void shouldExportEmptyData() throws Exception {
        String token = createEmptyLedger();

        mockMvc.perform(get("/export/csv?ledgerId=" + testLedgerId)
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(header().exists("Content-Disposition"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("日期,类型,金额,分类,子分类,备注")));
    }

    /**
     * 创建测试数据
     */
    private String createTestData() {
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

        // 创建3笔账单
        Transaction tx1 = new Transaction();
        tx1.setLedgerId(ledger.getId());
        tx1.setUserId(user.getId());
        tx1.setType("expense");
        tx1.setAmount(35.00);
        tx1.setCategoryId("cat_001");
        tx1.setCategoryName("餐饮");
        tx1.setSubcategory("午餐");
        tx1.setDate(new Date(System.currentTimeMillis() - 86400000L * 3));
        tx1.setNote("公司食堂");
        tx1.setCreatedAt(new Date());
        tx1.setUpdatedAt(new Date());
        tx1.setIsDeleted(false);
        transactionRepository.save(tx1);

        Transaction tx2 = new Transaction();
        tx2.setLedgerId(ledger.getId());
        tx2.setUserId(user.getId());
        tx2.setType("income");
        tx2.setAmount(5000.00);
        tx2.setCategoryId("cat_101");
        tx2.setCategoryName("工资");
        tx2.setSubcategory("月工资");
        tx2.setDate(new Date(System.currentTimeMillis() - 86400000L * 5));
        tx2.setNote("三月份工资");
        tx2.setCreatedAt(new Date());
        tx2.setUpdatedAt(new Date());
        tx2.setIsDeleted(false);
        transactionRepository.save(tx2);

        Transaction tx3 = new Transaction();
        tx3.setLedgerId(ledger.getId());
        tx3.setUserId(user.getId());
        tx3.setType("expense");
        tx3.setAmount(120.00);
        tx3.setCategoryId("cat_002");
        tx3.setCategoryName("交通");
        tx3.setSubcategory("地铁");
        tx3.setDate(new Date(System.currentTimeMillis() - 86400000L * 1));
        tx3.setNote("上班通勤");
        tx3.setCreatedAt(new Date());
        tx3.setUpdatedAt(new Date());
        tx3.setIsDeleted(false);
        transactionRepository.save(tx3);

        return jwtUtil.generateToken(user.getId(), user.getPhone());
    }

    /**
     * 创建空账本
     */
    private String createEmptyLedger() {
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
