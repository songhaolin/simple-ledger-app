package com.ledger.dto;

import java.util.List;

/**
 * 收支统计DTO
 */
public class StatisticsSummary {

    private Double totalIncome;
    private Double totalExpense;
    private Double balance;
    private List<CategorySummary> categorySummary;
    private List<DailyTrend> dailyTrend;

    // Getters and Setters
    public Double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(Double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public Double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(Double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public List<CategorySummary> getCategorySummary() {
        return categorySummary;
    }

    public void setCategorySummary(List<CategorySummary> categorySummary) {
        this.categorySummary = categorySummary;
    }

    public List<DailyTrend> getDailyTrend() {
        return dailyTrend;
    }

    public void setDailyTrend(List<DailyTrend> dailyTrend) {
        this.dailyTrend = dailyTrend;
    }

    /**
     * 分类汇总
     */
    public static class CategorySummary {
        private String categoryId;
        private String categoryName;
        private String type;
        private Double amount;
        private Double percentage;

        // Getters and Setters
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }

        public Double getPercentage() {
            return percentage;
        }

        public void setPercentage(Double percentage) {
            this.percentage = percentage;
        }
    }

    /**
     * 每日趋势
     */
    public static class DailyTrend {
        private String date;
        private Double income;
        private Double expense;

        // Getters and Setters
        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public Double getIncome() {
            return income;
        }

        public void setIncome(Double income) {
            this.income = income;
        }

        public Double getExpense() {
            return expense;
        }

        public void setExpense(Double expense) {
            this.expense = expense;
        }
    }
}
