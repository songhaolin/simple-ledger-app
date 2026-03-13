package com.ledger.service;

import com.ledger.exception.BusinessException;
import com.ledger.exception.BusinessException.ErrorCodes;
import com.ledger.model.Category;
import com.ledger.model.Transaction;
import com.ledger.repository.CategoryRepository;
import com.ledger.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * CSV导入服务
 */
@Service
public class ImportService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    public ImportService(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository) {
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository;
    }

    /**
     * 导入结果
     */
    public static class ImportResult {
        private int successCount;
        private int failureCount;
        private List<String> errors;

        public ImportResult(int successCount, int failureCount, List<String> errors) {
            this.successCount = successCount;
            this.failureCount = failureCount;
            this.errors = errors;
        }

        public int getSuccessCount() {
            return successCount;
        }

        public void setSuccessCount(int successCount) {
            this.successCount = successCount;
        }

        public int getFailureCount() {
            return failureCount;
        }

        public void setFailureCount(int failureCount) {
            this.failureCount = failureCount;
        }

        public List<String> getErrors() {
            return errors;
        }

        public void setErrors(List<String> errors) {
            this.errors = errors;
        }
    }

    /**
     * 从CSV导入账单
     */
    public ImportResult importFromCsv(
            MultipartFile file,
            String ledgerId,
            String userId) {
        List<String> errors = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        try {
            // 1. 读取CSV文件
            List<String[]> rows = readCsvFile(file);
            if (rows.isEmpty()) {
                throw new BusinessException(
                        ErrorCodes.INVALID_PARAM,
                        "CSV文件为空"
                );
            }

            // 2. 验证CSV格式（检查表头）
            validateCsvHeader(rows.get(0));

            // 3. 加载分类映射
            Map<String, String> categoryMap = buildCategoryMap();

            // 4. 获取"其他"分类ID
            String otherCategoryId = categoryMap.get("其他");

            // 5. 处理数据行
            for (int i = 1; i < rows.size(); i++) {
                try {
                    String[] row = rows.get(i);

                    // 跳过空行
                    if (row == null || row.length == 0) {
                        continue;
                    }

                    // 解析行数据
                    CsvRowData data = parseCsvRow(row, i + 1);

                    // 验证数据
                    validateCsvData(data, i + 1);

                    // 映射分类
                    String categoryId = categoryMap.get(data.category);
                    if (categoryId == null) {
                        categoryId = otherCategoryId;
                        data.category = "其他";
                    }

                    // 创建账单
                    createTransaction(
                            ledgerId,
                            userId,
                            data,
                            categoryId
                    );

                    successCount++;

                } catch (Exception e) {
                    failureCount++;
                    errors.add("第" + (i + 1) + "行: " + e.getMessage());
                }
            }

        } catch (Exception e) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "导入失败: " + e.getMessage()
            );
        }

        return new ImportResult(successCount, failureCount, errors);
    }

    /**
     * 读取CSV文件
     */
    private List<String[]> readCsvFile(MultipartFile file) throws Exception {
        List<String[]> rows = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                // 简单的CSV解析（按逗号分割，不处理引号内的逗号）
                String[] row = line.split(",");
                rows.add(row);
            }
        }

        return rows;
    }

    /**
     * 验证CSV表头
     */
    private void validateCsvHeader(String[] header) {
        if (header == null || header.length != 5) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "CSV格式错误：必须包含5列（日期、类型、金额、分类、备注）"
            );
        }

        String[] expectedHeaders = {"日期", "类型", "金额", "分类", "备注"};
        for (int i = 0; i < expectedHeaders.length; i++) {
            if (!header[i].trim().equals(expectedHeaders[i])) {
                throw new BusinessException(
                        ErrorCodes.INVALID_PARAM,
                        "CSV格式错误：第" + (i + 1) + "列应该是'" + expectedHeaders[i] + "'"
                );
            }
        }
    }

    /**
     * 构建分类映射表
     */
    private Map<String, String> buildCategoryMap() {
        Map<String, String> map = new HashMap<>();

        List<Category> categories = categoryRepository.findAll();
        for (Category category : categories) {
            map.put(category.getName(), category.getId());
        }

        return map;
    }

    /**
     * 解析CSV行数据
     */
    private CsvRowData parseCsvRow(String[] row, int lineNumber) {
        if (row == null || row.length != 5) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "列数不正确"
            );
        }

        CsvRowData data = new CsvRowData();
        data.dateStr = row[0].trim();
        data.type = row[1].trim();
        data.amountStr = row[2].trim();
        data.category = row[3].trim();
        data.note = row[4].trim();

        return data;
    }

    /**
     * 验证CSV行数据
     */
    private void validateCsvData(CsvRowData data, int lineNumber) {
        // 验证日期
        if (data.dateStr == null || data.dateStr.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "日期不能为空"
            );
        }

        // 验证日期格式（yyyy-MM-dd）
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);

        try {
            data.date = dateFormat.parse(data.dateStr);
        } catch (ParseException e) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "日期格式不正确，应为yyyy-MM-dd"
            );
        }

        // 验证类型
        if (data.type == null || data.type.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "类型不能为空"
            );

        }
        if (!data.type.equals("income") && !data.type.equals("expense")) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "类型不正确，应为income或expense"
            );
        }

        // 验证金额
        if (data.amountStr == null || data.amountStr.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "金额不能为空"
            );
        }

        try {
            data.amount = Double.parseDouble(data.amountStr);
            if (data.amount <= 0) {
                throw new BusinessException(
                        ErrorCodes.INVALID_PARAM,
                        "金额必须大于0"
                );
            }
        } catch (NumberFormatException e) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "金额格式不正确"
            );
        }

        // 验证分类
        if (data.category == null || data.category.isEmpty()) {
            throw new BusinessException(
                    ErrorCodes.INVALID_PARAM,
                    "分类不能为空"
            );
        }

        // 备注可以为空，不需要验证
    }

    /**
     * 创建账单
     */
    private void createTransaction(
            String ledgerId,
            String userId,
            CsvRowData data,
            String categoryId) {
        Transaction transaction = new Transaction();
        transaction.setLedgerId(ledgerId);
        transaction.setUserId(userId);
        transaction.setType(data.type);
        transaction.setAmount(data.amount);
        transaction.setCategoryId(categoryId);
        transaction.setCategoryName(data.category);
        transaction.setSubcategory(data.category);
        transaction.setDate(data.date);
        transaction.setNote(data.note.isEmpty() ? null : data.note);
        transaction.setCreatedAt(new Date());
        transaction.setUpdatedAt(new Date());
        transaction.setIsDeleted(false);

        transactionRepository.save(transaction);
    }

    /**
     * CSV行数据
     */
    private static class CsvRowData {
        String dateStr;
        String type;
        String amountStr;
        String category;
        String note;

        Date date;
        Double amount;
    }
}
