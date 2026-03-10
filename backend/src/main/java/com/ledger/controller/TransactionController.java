package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

/**
 * 账单控制器
 */
@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * 创建账单
     */
    @PostMapping
    public Response<Map<String, String>> createTransaction(
            @RequestBody CreateTransactionRequest request,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId（JWT拦截器已验证）
        String userId = (String) httpRequest.getAttribute("userId");

        // 创建账单
        Transaction transaction = transactionService.createTransaction(
                request.getLedgerId(),
                userId,
                request.getType(),
                request.getAmount(),
                request.getCategoryId(),
                request.getSubcategory(),
                request.getDate() != null ? request.getDate() : new Date(),
                request.getNote(),
                request.getImages()
        );

        // 返回transactionId
        Map<String, String> data = new HashMap<>();
        data.put("transactionId", transaction.getId());

        return Response.success(data);
    }

    /**
     * 创建账单请求体
     */
    @lombok.Data
    public static class CreateTransactionRequest {
        private String ledgerId;
        private String type;
        private Double amount;
        private String categoryId;
        private String subcategory;
        private Date date;
        private String note;
        private java.util.List<String> images;
    }
}
