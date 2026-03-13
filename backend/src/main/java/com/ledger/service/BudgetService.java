package com.ledger.service;

import com.ledger.dto.BudgetStatistics;
import com.ledger.dto.SetBudgetRequest;
import com.ledger.dto.UpdateBudgetRequest;
import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.model.Budget;
import com.ledger.model.Transaction;
import com.ledger.repository.BudgetRepository;
import com.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 预算管理服务
 */
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionRepository transactionRepository;

    public BudgetService(BudgetRepository budgetRepository, TransactionRepository transactionRepository) {
        this.budgetRepository = budgetRepository;
        this.transactionRepository = transactionRepository;
    }

    /**
     * 设置分类预算
     */
    public String setBudgets(String ledgerId, List<SetBudgetRequest.CategoryBudget> budgets) {
        // 1. 删除该账本原有预算
        budgetRepository.deleteByLedgerId(ledgerId);

        // 2. 创建新预算
        List<Budget> budgetList = new ArrayList<>();
        Date now = new Date();

        for (SetBudgetRequest.CategoryBudget budgetRequest : budgets) {
            Budget budget = new Budget();
            budget.setId(java.util.UUID.randomUUID().toString());
            budget.setLedgerId(ledgerId);
            budget.setCategoryId(budgetRequest.getCategoryId());
            budget.setAmount(budgetRequest.getAmount());
            budget.setPeriod("monthly");
            budget.setSpentAmount(0.0);
            budget.setCreatedAt(now);
            budget.setUpdatedAt(now);
            budgetList.add(budget);
        }

        budgetRepository.saveAll(budgetList);

        // 3. 返回成功消息
        return "预算设置成功，共设置" + budgets.size() + "个分类预算";
    }

    /**
     * 更新分类预算
     */
    public String updateBudget(String ledgerId, String categoryId, Double newAmount) {
        // 1. 查找现有预算
        Budget budget = budgetRepository.findByLedgerIdAndCategoryId(ledgerId, categoryId);
        if (budget == null) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "该分类未设置预算"
            );
        }

        // 2. 更新预算金额
        budget.setAmount(newAmount);
        budget.setUpdatedAt(new Date());

        budgetRepository.save(budget);

        return "预算更新成功";
    }

    /**
     * 获取预算统计
     */
    public BudgetStatistics getBudgetStatistics(String ledgerId) {
        // 1. 查找所有预算
        List<Budget> budgets = budgetRepository.findByLedgerId(ledgerId);

        // 2. 计算本月支出（按分类）
        Map<String, Double> spentByCategory = calculateSpentByCategory(ledgerId);

        // 3. 组装统计数据
        double totalBudget = 0.0;
        double totalExpense = 0.0;
        List<BudgetStatistics.CategoryBudgetSummary> categorySummaries = new ArrayList<>();
        List<String> overBudgetCategories = new ArrayList<>();

        for (Budget budget : budgets) {
            Double spent = spentByCategory.getOrDefault(budget.getCategoryId(), 0.0);
            Double remaining = budget.getAmount() - spent;

            // 更新已使用金额
            budget.setSpentAmount(spent);

            totalBudget += budget.getAmount();
            totalExpense += spent;

            // 判断是否超支
            boolean isOverBudget = spent > budget.getAmount();

            BudgetStatistics.CategoryBudgetSummary summary = new BudgetStatistics.CategoryBudgetSummary();
            summary.setCategoryId(budget.getCategoryId());
            summary.setCategoryName(budget.getCategoryName());
            summary.setBudget(budget.getAmount());
            summary.setSpent(spent);
            summary.setIsOverBudget(isOverBudget);
            summary.setPercentage(budget.getAmount() > 0 ? (spent / budget.getAmount() * 100) : 0);
            categorySummaries.add(summary);

            if (isOverBudget) {
                overBudgetCategories.add(budget.getCategoryId());
            }
        }

        // 4. 保存更新后的预算数据
        budgetRepository.saveAll(budgets);

        // 5. 组装返回数据
        BudgetStatistics statistics = new BudgetStatistics();
        statistics.setTotalBudget(totalBudget);
        statistics.setTotalExpense(totalExpense);
        statistics.setRemainingBudget(totalBudget - totalExpense);
        statistics.setCategoryBudgets(categorySummaries);
        statistics.setOverBudgetCategories(overBudgetCategories);

        return statistics;
    }

    /**
     * 计算本月各分类的支出
     */
    private Map<String, Double> calculateSpentByCategory(String ledgerId) {
        // 获取本月第一天的日期
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        Date startDate = cal.getTime();
        Date endDate = new Date();

        // 查询本月所有支出账单
        List<Transaction> transactions = transactionRepository.findByLedgerIdAndDateBetweenOrderByDateDesc(
                ledgerId,
                startDate,
                endDate,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent();

        // 按分类汇总
        Map<String, Double> spentByCategory = new HashMap<>();

        for (Transaction tx : transactions) {
            if (!tx.getIsDeleted() && "expense".equals(tx.getType())) {
                String categoryId = tx.getCategoryId();
                spentByCategory.put(categoryId, spentByCategory.getOrDefault(categoryId, 0.0) + tx.getAmount());
            }
        }

        return spentByCategory;
    }
}
