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

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * CSV导入功能测试 - TDD
 */
@SpringBootTest
@AutoConfigureMockMvc
public class ImportControllerTest {

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
    private String expenseCategoryId;
    private String incomeCategoryId;
    private String otherCategoryId;

    @BeforeEach
    public void setup() {
        // 清理测试数据
        transactionRepository.deleteAll();
        categoryRepository.deleteAll();
        ledgerRepository.deleteAll();
        userRepository.deleteAll();

        // 创建测试用户
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

        // 创建测试账本
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

        // 创建测试分类
        // 支出分类
        Category expenseCat = new Category();
        expenseCat.setName("餐饮");
        expenseCat.setIcon("🍔");
        expenseCat.setColor("#FF5722");
        expenseCat.setType("expense");
        expenseCat.setIsDefault(true);
        expenseCat.setSortOrder(1);
        expenseCat = categoryRepository.save(expenseCat);
        expenseCategoryId = expenseCat.getId();

        // 收入分类
        Category incomeCat = new Category();
        incomeCat.setName("工资");
        incomeCat.setIcon("💰");
        incomeCat.setColor("#4CAF50");
        incomeCat.setType("income");
        incomeCat.setIsDefault(true);
        incomeCat.setSortOrder(2);
        incomeCat = categoryRepository.save(incomeCat);
        incomeCategoryId = incomeCat.getId();

        // 其他分类
        Category otherCat = new Category();
        otherCat.setName("其他");
        otherCat.setIcon("📦");
        otherCat.setColor("#9E9E9E");
        otherCat.setType("expense");
        otherCat.setIsDefault(true);
        otherCat.setSortOrder(99);
        otherCat = categoryRepository.save(otherCat);
        otherCategoryId = otherCat.getId();
    }

    /**
     * 测试1：正常导入CSV文件（支出）
     */
    @Test
    public void shouldImportExpenseCsvSuccessfully() throws Exception {
        // 准备CSV数据
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,午餐\n" +
                "2025-01-02,expense,50.0,餐饮,晚餐\n" +
                "2025-01-03,expense,120.0,餐饮,聚餐";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        // 打印响应内容（用于调试）
        String response = mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andReturn()
                .getResponse()
                .getContentAsString();
        System.out.println("Response: " + response);

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failureCount").value(0))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors").isEmpty());

        // 验证数据库中的记录
        List<Transaction> transactions = transactionRepository.findByLedgerId(testLedgerId);
        org.junit.jupiter.api.Assertions.assertEquals(3, transactions.size());
    }

    /**
     * 测试2：正常导入CSV文件（收入）
     */
    @Test
    public void shouldImportIncomeCsvSuccessfully() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,income,10000.0,工资,1月工资\n" +
                "2025-01-15,income,5000.0,工资,奖金";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(2))
                .andExpect(jsonPath("$.data.failureCount").value(0));
    }

    /**
     * 测试3：导入不存在的分类，自动映射到"其他"
     */
    @Test
    public void shouldMapUnknownCategoryToOther() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,100.0,不存在的分类,测试\n" +
                "2025-01-02,expense,50.0,餐饮,正常";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(2));

        // 验证第一条记录被映射到"其他"分类
        List<Transaction> transactions = transactionRepository.findByLedgerId(testLedgerId);
        org.junit.jupiter.api.Assertions.assertEquals(2, transactions.size());

        boolean hasOtherCategory = transactions.stream()
                .anyMatch(t -> t.getCategoryName().equals("其他"));
        org.junit.jupiter.api.Assertions.assertTrue(hasOtherCategory, "应该有记录映射到'其他'分类");
    }

    /**
     * 测试4：CSV格式错误（缺少列）
     */
    @Test
    public void shouldFailWithInvalidCsvFormat() throws Exception {
        // 缺少"备注"列
        String csvContent = "日期,类型,金额,分类\n" +
                "2025-01-01,expense,35.5,餐饮";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.code").value("4001"))
                .andExpect(jsonPath("$.message").value(containsString("CSV格式")));
    }

    /**
     * 测试5：CSV数据验证失败（无效金额）
     */
    @Test
    public void shouldFailWithInvalidAmount() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,-10.0,餐饮,负数金额\n" +
                "2025-01-02,expense,0.0,餐饮,零金额";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(0))
                .andExpect(jsonPath("$.data.failureCount").value(2))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors", hasSize(2)));
    }

    /**
     * 测试6：CSV数据验证失败（无效类型）
     */
    @Test
    public void shouldFailWithInvalidType() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,invalid,100.0,餐饮,无效类型";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(0))
                .andExpect(jsonPath("$.data.failureCount").value(1))
                .andExpect(jsonPath("$.data.errors[0]").value(containsString("类型")));
    }

    /**
     * 测试7：CSV数据验证失败（无效日期格式）
     */
    @Test
    public void shouldFailWithInvalidDateFormat() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025/01/01,expense,100.0,餐饮,错误日期格式\n" +
                "2025-01-02,expense,50.0,餐饮,正确日期";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(1))
                .andExpect(jsonPath("$.data.failureCount").value(1))
                .andExpect(jsonPath("$.data.errors[0]").value(containsString("日期")));
    }

    /**
     * 测试8：空文件
     */
    @Test
    public void shouldFailWithEmptyFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                "".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("文件")));
    }

    /**
     * 测试9：非CSV文件
     */
    @Test
    public void shouldFailWithNonCsvFile() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.txt",
                "text/plain",
                "some text".getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("CSV")));
    }

    /**
     * 测试10：无Token访问
     */
    @Test
    public void shouldFailWithoutToken() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,午餐";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isUnauthorized());
    }

    /**
     * 测试11：缺少ledgerId参数
     */
    @Test
    public void shouldFailWithoutLedgerId() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,午餐";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv")
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }

    /**
     * 测试12：批量导入（100条记录）
     */
    @Test
    public void shouldImportLargeCsvFile() throws Exception {
        StringBuilder csvBuilder = new StringBuilder("日期,类型,金额,分类,备注\n");

        for (int i = 1; i <= 100; i++) {
            csvBuilder.append(String.format("2025-01-%02d,expense,%.2f,餐饮,测试%d\n",
                    (i % 30) + 1, 10.0 + i, i));
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvBuilder.toString().getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(100))
                .andExpect(jsonPath("$.data.failureCount").value(0));
    }

    /**
     * 测试13：部分成功部分失败
     */
    @Test
    public void shouldHandlePartialSuccess() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,正常1\n" +
                "2025-01-02,expense,-10.0,餐饮,无效金额\n" +
                "2025-01-03,expense,50.0,餐饮,正常2\n" +
                "2025-01-04,invalid,100.0,餐饮,无效类型\n" +
                "2025-01-05,expense,20.0,餐饮,正常3";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(3))
                .andExpect(jsonPath("$.data.failureCount").value(2))
                .andExpect(jsonPath("$.data.errors").isArray())
                .andExpect(jsonPath("$.data.errors", hasSize(2)));
    }

    /**
     * 测试14：备注字段可以为空
     */
    @Test
    public void shouldHandleEmptyNote() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,\n" +
                "2025-01-02,expense,50.0,餐饮,有备注";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.successCount").value(2));
    }

    /**
     * 测试15：文件太大（超过限制）
     */
    @Test
    public void shouldFailWithTooLargeFile() throws Exception {
        // 创建超过5MB的CSV
        StringBuilder csvBuilder = new StringBuilder("日期,类型,金额,分类,备注\n");
        for (int i = 0; i < 100000; i++) {
            csvBuilder.append(String.format("2025-01-01,expense,%.2f,餐饮,很长的备注内容%s\n",
                    10.0, "x".repeat(100)));
        }

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvBuilder.toString().getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(multipart("/import/csv?ledgerId=" + testLedgerId)
                        .file(file)
                        .header("Authorization", "Bearer " + testToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value(containsString("大小")));
    }
}
