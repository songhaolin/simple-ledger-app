package com.ledger.controller;

import com.ledger.model.Transaction;
import com.ledger.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 账单控制器
 */
@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * 创建账单
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> createTransaction(
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
                request.getDate() != null ? request.getDate() : new java.util.Date(),
                request.getNote(),
                request.getImages()
        );

        // 返回transactionId
        Map<String, String> data = new HashMap<>();
        data.put("transactionId", transaction.getId());

        return ApiResponse.success(data);
    }

    /**
     * 获取账单列表（分页）
     */
    @GetMapping
    public ResponseEntity<ApiResponse<PagedResult<Transaction>>> getTransactions(
            @RequestParam(required = true) String ledgerId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 查询账单列表
        org.springframework.data.domain.Page<Transaction> transactionPage = transactionService.getTransactions(
                ledgerId,
                page,
                size
        );

        // 封装分页结果
        PagedResult<Transaction> result = PagedResult.fromPage(transactionPage);

        return ApiResponse.success(result);
    }

    /**
     * 更新账单
     */
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> updateTransaction(
            @PathVariable String transactionId,
            @RequestBody UpdateTransactionRequest request,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 更新账单
        Transaction transaction = transactionService.updateTransaction(
                transactionId,
                userId,
                request.getAmount(),
                request.getCategoryId(),
                request.getSubcategory(),
                request.getDate() != null ? request.getDate() : new java.util.Date(),
                request.getNote(),
                request.getImages()
        );

        return ApiResponse.success(transaction);
    }

    /**
     * 删除账单（软删除）
     */
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<String>> deleteTransaction(
            @PathVariable String transactionId,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 删除账单
        transactionService.deleteTransaction(transactionId, userId);

        // 返回成功消息
        return ApiResponse.success("删除成功");
    }

    /**
     * 获取账单详情（功能9）
     */
    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<Transaction>> getTransaction(
            @PathVariable String transactionId,
            HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 获取账单详情
        Transaction transaction = transactionService.getTransaction(transactionId, userId);

        return ApiResponse.success(transaction);
    }

    /**
     * 创建账单请求体
     */
    public static class CreateTransactionRequest {
        private String ledgerId;
        private String type;
        private Double amount;
        private String categoryId;
        private String subcategory;
        private java.util.Date date;
        private String note;
        private java.util.List<String> images;

        public String getLedgerId() {
            return ledgerId;
        }

        public String getType() {
            return type;
        }

        public Double getAmount() {
            return amount;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getSubcategory() {
            return subcategory;
        }

        public java.util.Date getDate() {
            return date;
        }

        public String getNote() {
            return note;
        }

        public java.util.List<String> getImages() {
            return images;
        }

        public void setLedgerId(String ledgerId) {
            this.ledgerId = ledgerId;
        }

        public void setType(String type) {
            this.type = type;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
        }

        public void setDate(java.util.Date date) {
            this.date = date;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setImages(java.util.List<String> images) {
            this.images = images;
        }
    }

    /**
     * 更新账单请求体
     */
    public static class UpdateTransactionRequest {
        private Double amount;
        private String categoryId;
        private String subcategory;
        private java.util.Date date;
        private String note;
        private java.util.List<String> images;

        public Double getAmount() {
            return amount;
        }

        public String getCategoryId() {
            return categoryId;
        }

        public String getSubcategory() {
            return subcategory;
        }

        public java.util.Date getDate() {
            return date;
        }

        public String getNote() {
            return note;
        }

        public java.util.List<String> getImages() {
            return images;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public void setSubcategory(String subcategory) {
            this.subcategory = subcategory;
        }

        public void setDate(java.util.Date date) {
            this.date = date;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public void setImages(java.util.List<String> images) {
            this.images = images;
        }
    }

    /**
     * 分页结果
     */
    public static class PagedResult<T> {
        private java.util.List<T> content;
        private int currentPage;
        private long totalElements;
        private int totalPages;
        private int pageSize;

        // Getters
        public java.util.List<T> getContent() {
            return content;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public long getTotalElements() {
            return totalElements;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getPageSize() {
            return pageSize;
        }

        // Setters
        public void setContent(java.util.List<T> content) {
            this.content = content;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public void setTotalElements(long totalElements) {
            this.totalElements = totalElements;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        /**
         * 从 Spring Data Page 创建
         */
        public static <T> PagedResult<T> fromPage(org.springframework.data.domain.Page<T> page) {
            PagedResult<T> result = new PagedResult<>();
            result.setContent(page.getContent());
            result.setCurrentPage(page.getNumber() + 1); // 页码从1开始
            result.setTotalElements(page.getTotalElements());
            result.setTotalPages(page.getTotalPages());
            result.setPageSize(page.getSize());
            return result;
        }
    }
}
