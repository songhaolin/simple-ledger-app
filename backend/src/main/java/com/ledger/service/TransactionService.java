package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 账单服务
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

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
            Date date,
            String note,
            java.util.List<String> images
    ) {
        // 1. 验证必填字段
        if (ledgerId == null || ledgerId.isEmpty()) {
            throw new BusinessException("4001", "账本ID不能为空");
        }

        if (type == null || (!type.equals("income") && !type.equals("expense") && !type.equals("transfer"))) {
            throw new BusinessException("4001", "类型不正确");
        }

        if (amount == null || amount <= 0) {
            throw new BusinessException("4001", "金额必须大于0");
        }

        // 2. 创建账单对象
        Transaction transaction = new Transaction();
        transaction.setLedgerId(ledgerId);
        transaction.setUserId(userId);
        transaction.setType(type);
        transaction.setAmount(amount);
        transaction.setCategoryId(categoryId);
        transaction.setCategoryName(subcategory);
        transaction.setSubcategory(subcategory);
        transaction.setDate(date);
        transaction.setNote(note);
        transaction.setImages(images);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());
        transaction.setIsDeleted(false);

        // 3. 保存到数据库
        return transactionRepository.save(transaction);
    }
}
