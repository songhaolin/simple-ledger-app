package com.ledger.service;

import com.ledger.model.Transaction;
import com.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 数据导出服务
 */
@Service
public class ExportService {

    private final TransactionRepository transactionRepository;

    public ExportService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * 导出CSV
     */
    public byte[] exportCsv(String ledgerId, java.util.Date startDate, java.util.Date endDate) {
        // 1. 设置默认日期范围
        if (startDate == null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.set(java.util.Calendar.DAY_OF_MONTH, 1);
            cal.set(java.util.Calendar.HOUR_OF_DAY, 0);
            cal.set(java.util.Calendar.MINUTE, 0);
            cal.set(java.util.Calendar.SECOND, 0);
            startDate = cal.getTime();
        }

        if (endDate == null) {
            endDate = new java.util.Date();
        }

        // 2. 查询账单数据
        List<Transaction> transactions = transactionRepository.findByLedgerIdAndDateBetweenOrderByDateDesc(
                ledgerId,
                startDate,
                endDate,
                org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)
        ).getContent();

        // 3. 生成CSV内容
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(outputStream, false, StandardCharsets.UTF_8)) {

            // 写入CSV表头
            writer.println("日期,类型,金额,分类,子分类,备注");

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // 写入CSV数据行
            for (Transaction tx : transactions) {
                if (!tx.getIsDeleted()) {
                    String type = "income".equals(tx.getType()) ? "收入" : ("expense".equals(tx.getType()) ? "支出" : tx.getType());
                    String amount = String.format("%.2f", tx.getAmount());
                    String note = tx.getNote() != null ? tx.getNote() : "";
                    String subcategory = tx.getSubcategory() != null ? tx.getSubcategory() : "";

                    writer.println(String.format("%s,%s,%s,%s,%s,%s",
                            dateFormat.format(tx.getDate()),
                            type,
                            amount,
                            tx.getCategoryName(),
                            subcategory,
                            note.replace(",", "，") // 转义逗号
                    ));
                }
            }

            writer.flush();
            return outputStream.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("CSV导出失败", e);
        }
    }
}
