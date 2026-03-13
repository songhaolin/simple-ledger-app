package com.ledger.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * 账单实体
 */
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

    private Boolean isDeleted;        // 是否软删除

    // Getters
    public String getId() {
        return id;
    }

    public String getLedgerId() {
        return ledgerId;
    }

    public String getUserId() {
        return userId;
    }

    public String getType() {
        return type;
    }

    public Double getAmount() {
        return amount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public Date getDate() {
        return date;
    }

    public String getNote() {
        return note;
    }

    public List<String> getImages() {
        return images;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setLedgerId(String ledgerId) {
        this.ledgerId = ledgerId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setImages(List<String> images) {
        this.images = images;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }
}
