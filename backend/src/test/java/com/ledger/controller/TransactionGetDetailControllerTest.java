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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 获取账单详情测试 - TDD
 *
 * 测试目标：GET /api/v1/transactions/{transactionId}
 * 场景：
 * 1. 正常获取账单详情
 * 2. Token无效（401）
 * 3. 账单不存在（404）
 * 4. 无权查看（非创建者/非账本成员）
 */
@SpringBootTest(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/ledger_test",
    "jwt.secret=my-super-secret-key-for-testing-purposes-only-please-use-a-longer-key-in-production-environment-at-least-512-bits-long-here-is-more-text-to-make-it-long-enough-for-hs512-algorithm-to-work-correctly",
    "jwt.expiration=604800000"
})
@AutoConfigureMockMvc
public class TransactionGetDetailControllerTest {

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
     * 测试1：正常获取账单详情
     */
    @Test
    public void shouldGetTransactionDetailSuccessfully() throws Exception {
        // 1. 创建用户和账本
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
        tx.setImages(java.util.Arrays.asList("http://example.com/image1.jpg"));
        tx.setCreatedAt(new java.util.Date());
        tx.setUpdatedAt(new java.util.Date());
        tx.setIsDeleted(false);
        tx = transactionRepository.save(tx);

        // 3. 生成Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        // 4. 获取账单详情
        mockMvc.perform(get("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.id").value(tx.getId()))
                .andExpect(jsonPath("$.data.type").value("expense"))
                .andExpect(jsonPath("$.data.amount").value(35.0))
                .andExpect(jsonPath("$.data.categoryName").value("餐饮"))
                .andExpect(jsonPath("$.data.subcategory").value("午餐"))
                .andExpect(jsonPath("$.data.note").value("公司楼下"))
                .andExpect(jsonPath("$.data.images[0]").value("http://example.com/image1.jpg"))
                .andExpect(jsonPath("$.data.userId").value(user.getId()))
                .andExpect(jsonPath("$.data.isDeleted").value(false));
    }

    /**
     * 测试2：Token无效
     */
    @Test
    public void shouldFailWithInvalidToken() throws Exception {
        String txId = "tx_001";

        mockMvc.perform(get("/transactions/" + txId)
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

        // 尝试获取不存在的账单
        mockMvc.perform(get("/transactions/nonexistent-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }

    /**
     * 测试4：无权查看（非创建者）
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

        // 2. 创建用户2（尝试查看）
        var user2 = new com.ledger.model.User();
        user2.setPhone("13800138002");
        user2.setPasswordHash(passwordEncoder.encode("Password123!"));
        user2.setNickname("李四");
        user2.setCreatedAt(new java.util.Date());
        user2.setUpdatedAt(new java.util.Date());
        user2.setIsActive(true);
        userRepository.save(user2);

        // 3. 创建账本（属于用户1）
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

        // 4. 创建账单（属于用户1）
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
        tx.setImages(java.util.Arrays.asList("http://example.com/image1.jpg"));
        tx.setCreatedAt(new java.util.Date());
        tx.setUpdatedAt(new java.util.Date());
        tx.setIsDeleted(false);
        tx = transactionRepository.save(tx);

        // 5. 用户2尝试查看（使用用户2的Token）
        String token = jwtUtil.generateToken(user2.getId(), user2.getPhone());

        mockMvc.perform(get("/transactions/" + tx.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
