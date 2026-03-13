package com.ledger.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

/**
 * 设置预算请求
 */
public class SetBudgetRequest {
    @jakarta.validation.constraints.NotBlank(message = "账本ID不能为空")
    private String ledgerId;
    
    @jakarta.validation.constraints.NotEmpty(message = "预算列表不能为空")
    private List<CategoryBudget> budgets;

    // Getters and Setters
    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public List<CategoryBudget> getBudgets() {
        return budgets;
    }

    public void setBudgets(List<CategoryBudget> budgets) {
        this.budgets = budgets;
    }

    /**
     * 分类预算
     */
    public static class CategoryBudget {
        @jakarta.validation.constraints.NotBlank(message = "分类ID不能为空")
        private String categoryId;
        
        @jakarta.validation.constraints.NotNull(message = "金额不能为空")
        @jakarta.validation.constraints.Positive(message = "金额必须大于0")
        private Double amount;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }
}
