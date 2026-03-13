package com.ledger.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ledger.model.Category;
import com.ledger.model.Ledger;
import com.ledger.model.Transaction;
import com.ledger.model.User;
import com.ledger.repository.CategoryRepository;
import com.ledger.repository.LedgerRepository;
import com.ledger.repository.TransactionRepository;
import com.ledger.repository.UserRepository;
import com.ledger.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CSV导入功能测试 - 简化版
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private String testUserId;
    private String testToken;
    private String testLedgerId;

    @BeforeEach
    public void setup() {
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        ledgerRepository.deleteAll();
        userRepository.deleteAll();

        User user = new User();
        user.setPhone("13800138000");
        user.setPasswordHash(new BCryptPasswordEncoder().encode("Password123!"));
        user.setNickname("测试用户");
        user.setCreatedAt(new Date());
        user.setUpdatedAt(new Date());
        user.setIsActive(true);
        user = userRepository.save(user);

        testUserId = user.getId();
        testToken = jwtUtil.generateToken(testUserId, user.getPhone());

        Ledger ledger = new Ledger();
        ledger.setName("测试账本");
        ledger.setType("personal");
        ledger.setOwnerId(testUserId);
        ledger.setBudget(4000);
        ledger.setCurrency("CNY");
        ledger.setCreatedAt(new Date());
        ledger.setUpdatedAt(new Date());

        List<Ledger.Member> members = new ArrayList<>();
        Ledger.Member member = new Ledger.Member();
        member.setUserId(testUserId);
        member.setRole("owner");
        member.setJoinedAt(new Date());
        members.add(member);
        ledger.setMembers(members);
        ledger = ledgerRepository.save(ledger);
        testLedgerId = ledger.getId();

        Category expenseCat = new Category();
        expenseCat.setName("餐饮");
        expenseCat.setIcon("🍔");
        expenseCat.setColor("#FF5722");
        expenseCat.setType("expense");
        expenseCat.setIsDefault(true);
        expenseCat.setSortOrder(1);
        categoryRepository.save(expenseCat);

        Category otherCat = new Category();
        otherCat.setName("其他");
        otherCat.setIcon("📦");
        otherCat.setColor("#9E9E9E");
        otherCat.setType("expense");
        otherCat.setIsDefault(true);
        otherCat.setSortOrder(99);
        categoryRepository.save(otherCat);
    }

    @Test
    public void shouldImportExpenseCsvSuccessfully() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,午餐";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/import/csv")
                        .file(file)
                        .param("ledgerId", testLedgerId)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(org.springframework.http.MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failureCount").value(0));
    }
}
