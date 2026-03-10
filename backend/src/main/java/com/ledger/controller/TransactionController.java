package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.service.TransactionService;
import com.ledger.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 账单控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/transactions")
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
                request.getDate(),
                request.getNote(),
                request.getImages()
        );

        // 返回transactionId
        Map<String, String> data = new HashMap<>();
        data.put("transactionId", transaction.getId());

        return Response.success(data);
    }

    /**
     * 获取账单列表（分页）
     */
    @GetMapping
    public Response<PagedResult<Transaction>> getTransactions(
            @RequestParam(required = true) String ledgerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 查询账单列表
        Page<Transaction> transactionPage = transactionService.getTransactions(
                ledgerId,
                page,
                size
        );

        // 封装分页结果
        PagedResult<Transaction> result = new PagedResult<>();
        result.setContent(transactionPage.getContent());
        result.setCurrentPage(page);
        result.setTotalElements(transactionPage.getTotalElements());
        result.setTotalPages(transactionPage.getTotalPages());
        result.setPageSize(size);

        return Response.success(result);
    }

    /**
     * 分页结果
     */
    @lombok.Data
    public static class PagedResult<T> {
        private java.util.List<T> content;
        private int currentPage;
        private long totalElements;
        private int totalPages;
        private int pageSize;
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
        private java.util.Date date;
        private String note;
        private java.util.List<String> images;
    }
}
