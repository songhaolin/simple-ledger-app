package com.ledger.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 分类实体
 */
@Data
@Document(collection = "categories")
public class Category {

    @Id
    private String id;

    private String parentId;    // 父分类ID

    private String name;        // 分类名称

    private String icon;        // 图标（emoji）

    private String color;       // 颜色（hex）

    private String type;        // 类型: income | expense

    private Boolean isDefault; // 是否预设分类

    private Integer sortOrder;  // 排序
}
