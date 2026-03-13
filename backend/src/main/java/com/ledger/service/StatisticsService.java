package com.ledger.service;

import com.ledger.dto.StatisticsSummary;
import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 收支统计服务
 */
@Service
public class StatisticsService {

    private final TransactionRepository transactionRepository;

    public StatisticsService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * 获取收支统计
     */
    public StatisticsSummary getStatistics(String ledgerId, Date startDate, Date endDate) {
        // 1. 设置默认日期范围
        if (startDate == null) {
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.DAY_OF_MONTH, 1);
            cal.set(Calendar.HOUR_OF_DAY, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            startDate = cal.getTime();
        }

        if (endDate == null) {
            endDate = new Date();
        }

        // 2. 查询账单数据
        List<Transaction> transactions = transactionRepository.findByLedgerIdAndDateBetweenOrderByDateDesc(
                ledgerId,
                startDate,
                endDate,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent();

        // 3. 计算总收支
        double totalIncome = 0.0;
        double totalExpense = 0.0;

        for (Transaction tx : transactions) {
            if (!tx.getIsDeleted()) {
                if ("income".equals(tx.getType())) {
                    totalIncome += tx.getAmount();
                } else if ("expense".equals(tx.getType())) {
                    totalExpense += tx.getAmount();
                }
            }
        }

        double balance = totalIncome - totalExpense;

        // 4. 按分类汇总
        List<StatisticsSummary.CategorySummary> categorySummary = calculateCategorySummary(transactions, totalExpense);

        // 5. 计算每日趋势
        List<StatisticsSummary.DailyTrend> dailyTrend = calculateDailyTrend(transactions, startDate, endDate);

        // 6. 组装返回数据
        StatisticsSummary summary = new StatisticsSummary();
        summary.setTotalIncome(totalIncome);
        summary.setTotalExpense(totalExpense);
        summary.setBalance(balance);
        summary.setCategorySummary(categorySummary);
        summary.setDailyTrend(dailyTrend);

        return summary;
    }

    /**
     * 计算分类汇总
     */
    private List<StatisticsSummary.CategorySummary> calculateCategorySummary(
            List<Transaction> transactions,
            double totalExpense) {
        
        // 按分类ID聚合
        Map<String, StatisticsSummary.CategorySummary> categoryMap = new HashMap<>();
        
        for (Transaction tx : transactions) {
            if (!tx.getIsDeleted() && "expense".equals(tx.getType())) {
                String key = tx.getCategoryId();
                StatisticsSummary.CategorySummary summary = categoryMap.get(key);
                
                if (summary == null) {
                    summary = new StatisticsSummary.CategorySummary();
                    summary.setCategoryId(tx.getCategoryId());
                    summary.setCategoryName(tx.getCategoryName());
                    summary.setType(tx.getType());
                    summary.setAmount(0.0);
                    categoryMap.put(key, summary);
                }
                
                summary.setAmount(summary.getAmount() + tx.getAmount());
            }
        }
        
        // 转换为列表并计算百分比
        List<StatisticsSummary.CategorySummary> summaryList = new ArrayList<>(categoryMap.values());
        
        for (StatisticsSummary.CategorySummary summary : summaryList) {
            if (totalExpense > 0) {
                summary.setPercentage((summary.getAmount() / totalExpense) * 100);
            } else {
                summary.setPercentage(0.0);
            }
        }
        
        // 按金额降序排序
        return summaryList.stream()
                .sorted((a, b) -> Double.compare(b.getAmount(), a.getAmount()))
                .collect(Collectors.toList());
    }

    /**
     * 计算每日趋势
     */
    private List<StatisticsSummary.DailyTrend> calculateDailyTrend(
            List<Transaction> transactions,
            Date startDate,
            Date endDate) {
        
        Map<String, StatisticsSummary.DailyTrend> dailyMap = new TreeMap<>(); // TreeMap自动按key排序
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        
        for (Transaction tx : transactions) {
            if (!tx.getIsDeleted()) {
                String dateKey = dateFormat.format(tx.getDate());
                StatisticsSummary.DailyTrend trend = dailyMap.get(dateKey);
                
                if (trend == null) {
                    trend = new StatisticsSummary.DailyTrend();
                    trend.setDate(dateKey);
                    trend.setIncome(0.0);
                    trend.setExpense(0.0);
                    dailyMap.put(dateKey, trend);
                }
                
                if ("income".equals(tx.getType())) {
                    trend.setIncome(trend.getIncome() + tx.getAmount());
                } else if ("expense".equals(tx.getType())) {
                    trend.setExpense(trend.getExpense() + tx.getAmount());
                }
            }
        }
        
        return new ArrayList<>(dailyMap.values());
    }
}
