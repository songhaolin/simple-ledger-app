package com.ledger.dto;

/**
 * 更新预算请求
 */
public class UpdateBudgetRequest {
    private String ledgerId;
    
    @jakarta.validation.constraints.NotNull(message = "金额不能为空")
    @jakarta.validation.constraints.Positive(message = "金额必须大于0")
    private Double amount;

    public String getLedgerId() {
        return ledgerId;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
