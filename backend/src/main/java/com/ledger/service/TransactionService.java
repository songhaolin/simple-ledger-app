package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
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
        if (categoryId == null || categoryId.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "分类ID不能为空"
            );
        }

        // 4. 验证类型
        if (type == null || (!type.equals("income") && !type.equals("expense") && !type.equals("transfer"))) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "类型不正确"
            );
        }

        // 5. 创建账单对象
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

        // 6. 保存到数据库
        return transactionRepository.save(transaction);
    }

    /**
     * 更新账单
     */
    public Transaction updateTransaction(
            String transactionId,
            String userId,
            Double amount,
            String categoryId,
            String subcategory,
            Date date,
            String note,
            java.util.List<String> images
    ) {
        // 1. 查找账单
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new BusinessException(
                        ErrorCodes.INVALID_PARAM,
                        "账单不存在"
                ));

        // 2. 验证权限（只有创建者可以更新）
        if (!transaction.getUserId().equals(userId)) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "无权修改此账单"
            );
        }

        // 3. 更新字段
        if (amount != null && amount > 0) {
            transaction.setAmount(amount);
        }

        if (categoryId != null && !categoryId.isEmpty()) {
            transaction.setCategoryId(categoryId);
            transaction.setCategoryName(subcategory);
            transaction.setSubcategory(subcategory);
        }

        if (date != null) {
            transaction.setDate(date);
        }

        if (note != null) {
            transaction.setNote(note);
        }

        if (images != null && !images.isEmpty()) {
            transaction.setImages(images);
        }

        // 4. 更新时间
        transaction.setUpdatedAt(new Date());

        // 5. 保存更新
        return transactionRepository.save(transaction);
    }

    /**
     * 获取账单列表（分页）
     */
    public org.springframework.data.domain.Page<Transaction> getTransactions(
            String ledgerId,
            int page,
            int size
    ) {
        // 计算起始日期（默认查询最近30天）
        Date startDate = new Date(System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000);
        Date endDate = new Date();

        // 创建分页和排序
        org.springframework.data.domain.PageRequest pageRequest =
                org.springframework.data.domain.PageRequest.of(
                        page - 1,  // Spring Data 页码从0开始
                        size,
                        org.springframework.data.domain.Sort.by(
                                org.springframework.data.domain.Sort.Direction.DESC,
                                "date"
                        )
                );

        return transactionRepository.findByLedgerIdAndDateBetweenOrderByDateDesc(
                ledgerId,
                startDate,
                endDate,
                pageRequest
        );
    }
}
