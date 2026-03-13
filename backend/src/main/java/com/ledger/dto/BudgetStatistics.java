package com.ledger.dto;

import java.util.List;

/**
 * 预算统计DTO
 */
public class BudgetStatistics {
    private Double totalBudget;        // 总预算
    private Double totalExpense;        // 总支出
    private Double remainingBudget;     // 剩余预算
    private List<CategoryBudgetSummary> categoryBudgets; // 分类预算统计
    private List<String> overBudgetCategories; // 超支分类

    // Getters and Setters
    public Double getTotalBudget() {
        return totalBudget;
    }

    public void setTotalBudget(Double totalBudget) {
        this.totalBudget = totalBudget;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getRemainingBudget() {
        return remainingBudget;
    }

    public void setRemainingBudget(Double remainingBudget) {
        this.remainingBudget = remainingBudget;
    }

    public List<CategoryBudgetSummary> getCategoryBudgets() {
        return categoryBudgets;
    }

    public void setCategoryBudgets(List<CategoryBudgetSummary> categoryBudgets) {
        this.categoryBudgets = categoryBudgets;
    }

    public List<String> getOverBudgetCategories() {
        return overBudgetCategories;
    }

    public void setOverBudgetCategories(List<String> overBudgetCategories) {
        this.overBudgetCategories = overBudgetCategories;
    }

    /**
     * 分类预算汇总
     */
    public static class CategoryBudgetSummary {
        private String categoryId;
        private String categoryName;
        private Double budget;
        private Double spent;
        private Double percentage;
        private Boolean isOverBudget;

        public String getCategoryId() {
            return categoryId;
        }

        public void setCategoryId(String categoryId) {
            this.categoryId = categoryId;
        }

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }

        public Double getBudget() {
            return budget;
        }

        public void setBudget(Double budget) {
            this.budget = budget;
        }

        public Double getSpent() {
            return spent;
        }

        public void setSpent(Double spent) {
            this.spent = spent;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }

        public Boolean getIsOverBudget() {
            return isOverBudget;
        }

        public void setIsOverBudget(Boolean isOverBudget) {
            this.isOverBudget = isOverBudget;
        }
    }
}
