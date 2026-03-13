package com.ledger.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 分类实体
 */
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

    // Getters
    public String getId() {
        return id;
    }

    public String getParentId() {
        return parentId;
    }

    public String getName() {
        return name;
    }

    public String getIcon() {
        return icon;
    }

    public String getColor() {
        return color;
    }

    public String getType() {
        return type;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
}
