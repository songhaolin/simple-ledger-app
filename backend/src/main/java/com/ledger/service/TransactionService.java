package com.ledger.service;

import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import com.ledger.model.Ledger;
import com.ledger.repository.LedgerRepository;
import com.ledger.repository.UserRepository;
import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

/**
 * 账单服务
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final LedgerRepository ledgerRepository;
    private final UserRepository userRepository;

    /**
     * 创建账单
     */
    public Transaction createTransaction(
            String ledgerId,
            String userId,
            String type,
            Double amount,
            String categoryId,
            String subcategory,
            java.util.Date date,
            String note,
            java.util.List<String> images
    ) {
        // 1. 验证账本ID
        if (ledgerId == null || ledgerId.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "账本ID不能为空"
            );
        }

        // 2. 验证金额
        if (amount == null || amount <= 0) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "金额必须大于0"
            );
        }

        // 3. 验证分类ID
        if (categoryId == null) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "分类ID不能为空"
            );
        }

        // 4. 创建账单对象
        Transaction transaction = new Transaction();
        transaction.setLedgerId(ledgerId);
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCategoryId(categoryId);
        transaction.setSubcategory(subcategory);
        transaction.setDate(date);
        transaction.setNote(note);
        transaction.setImages(images);
        transaction.setCreatedAt(new java.util.Date());
        transaction.setUpdatedAt(new java.util.Date());
        transaction.setIsDeleted(false);

        return transactionRepository.save(transaction);
    }

    /**
     * 获取账单列表（分页）
     */
    public Page<Transaction> getTransactions(
            String ledgerId,
            int page,
            int size
    ) {
        // 计算起始日期（默认查询最近30天）
        java.util.Date startDate = new java.util.Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        java.util.Date endDate = new java.util.Date();

        // 创建分页和排序
        PageRequest pageRequest = PageRequest.of(
                page - 1,  // Spring Data 页码从0开始
                size,
                Sort.by(Sort.Direction.DESC, "date")
        );

        return transactionRepository.findByLedgerIdAndDateBetweenOrderByDateDesc(
                ledgerId,
                startDate,
                endDate,
                pageRequest
        );
    }
}
