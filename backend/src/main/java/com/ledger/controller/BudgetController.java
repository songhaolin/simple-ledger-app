package com.ledger.controller;

import com.ledger.dto.BudgetStatistics;
import com.ledger.dto.SetBudgetRequest;
import com.ledger.dto.UpdateBudgetRequest;
import com.ledger.service.BudgetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 预算管理控制器
 */
@RestController
@RequestMapping("/budgets")
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * 设置分类预算
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Map<String, String>>> setBudgets(@RequestBody @Valid SetBudgetRequest request, HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 设置预算
        String message = budgetService.setBudgets(request.getLedgerId(), request.getBudgets());

        Map<String, String> data = new HashMap<>();
        data.put("message", message);
        return ApiResponse.success(data);
    }

    /**
     * 获取预算统计
     */
    @GetMapping("/statistics")
    public ResponseEntity<ApiResponse<BudgetStatistics>> getBudgetStatistics(@RequestParam(required = true) String ledgerId, HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 获取预算统计
        BudgetStatistics statistics = budgetService.getBudgetStatistics(ledgerId);

        return ApiResponse.success(statistics);
    }

    /**
     * 更新分类预算
     */
    @PutMapping("/{categoryId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> updateBudget(@PathVariable String categoryId, @RequestParam(required = false) String ledgerId, @RequestBody @Valid UpdateBudgetRequest request, HttpServletRequest httpRequest) {
        // 从请求属性中获取UserId
        String userId = (String) httpRequest.getAttribute("userId");

        // 如果请求体没有ledgerId，使用query参数
        String targetLedgerId = request.getLedgerId() != null ? request.getLedgerId() : ledgerId;

        // 更新预算
        String message = budgetService.updateBudget(targetLedgerId, categoryId, request.getAmount());

        Map<String, String> data = new HashMap<>();
        data.put("message", message);
        return ApiResponse.success(data);
    }
}
