package com.ledger.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * 账单实体
 */
@Data
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;                // 账单ID

    private String ledgerId;          // 账本ID（关联ledgers）

    private String userId;            // 记账用户ID

    private String type;              // 类型: "income" | "expense" | "transfer"

    private Double amount;            // 金额

    private String categoryId;        // 分类ID（关联categories）

    private String categoryName;      // 分类名称（冗余，提升查询）

    private String subcategory;       // 二级分类

    private Date date;               // 日期

    private String note;              // 备注

    private List<String> images;     // 图片URL列表

    private Date createdAt;          // 创建时间

    private Date updatedAt;           // 更新时间

    private Boolean isDeleted;        // 软删除标记
}
