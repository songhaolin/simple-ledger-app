package com.ledger.service;

import com.ledger.model.Category;
import com.ledger.model.Ledger;
import com.ledger.model.Transaction;
import com.ledger.model.User;
import com.ledger.repository.CategoryRepository;
import com.ledger.repository.LedgerRepository;
import com.ledger.repository.TransactionRepository;
import com.ledger.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * CSV导入服务测试
 */
@SpringBootTest
@ActiveProfiles("test")
public class ImportServiceTest {

    @Autowired
    private ImportService importService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LedgerRepository ledgerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    private String testUserId;
    private String testLedgerId;
    private String expenseCategoryId;
    private String otherCategoryId;

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
        expenseCat = categoryRepository.save(expenseCat);
        expenseCategoryId = expenseCat.getId();

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

    @Test
    public void testImportFromCsv_Success() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,35.5,餐饮,午餐\n" +
                "2025-01-02,expense,50.0,餐饮,晚餐";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportService.ImportResult result = importService.importFromCsv(
                file,
                testLedgerId,
                testUserId
        );

        assertEquals(2, result.getSuccessCount());
        assertEquals(0, result.getFailureCount());
        assertTrue(result.getErrors().isEmpty());

        List<Transaction> transactions = transactionRepository.findByLedgerId(testLedgerId);
        assertEquals(2, transactions.size());
    }

    @Test
    public void testImportFromCsv_UnknownCategory() throws Exception {
        String csvContent = "日期,类型,金额,分类,备注\n" +
                "2025-01-01,expense,100.0,不存在的分类,测试";

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "import.csv",
                "text/csv",
                csvContent.getBytes(StandardCharsets.UTF_8)
        );

        ImportService.ImportResult result = importService.importFromCsv(
                file,
                testLedgerId,
                testUserId
        );

        assertEquals(1, result.getSuccessCount());

        List<Transaction> transactions = transactionRepository.findByLedgerId(testLedgerId);
        assertEquals(1, transactions.size());
        assertEquals("其他", transactions.get(0).getCategoryName());
    }
}
