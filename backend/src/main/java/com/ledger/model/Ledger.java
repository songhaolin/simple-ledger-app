package com.ledger.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;
import java.util.List;

/**
 * 账本实体
 */
@Document(collection = "ledgers")
public class Ledger {

    @Id
    private String id;           // 账本ID

    private String name;           // 账本名称

    private String type;           // 类型: "personal" | "family"

    private String ownerId;       // 创建者ID（关联users）

    private List<Member> members;  // 成员列表

    private Integer budget;        // 总预算（可选）

    private String currency;       // 货币（默认"CNY"）

    private Date createdAt;       // 创建时间

    private Date updatedAt;       // 更新时间

    /**
     * 成员信息
     */
    public static class Member {
        private String userId;        // 用户ID
        private String role;           // 角色: "owner" | "member" | "viewer"
        private Date joinedAt;       // 加入时间

        // Getters
        public String getUserId() {
            return userId;
        }

        public String getRole() {
            return role;
        }

        public Date getJoinedAt() {
            return joinedAt;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public void setJoinedAt(Date joinedAt) {
            this.joinedAt = joinedAt;
        }
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public List<Member> getMembers() {
        return members;
    }

    public Integer getBudget() {
        return budget;
    }

    public String getCurrency() {
        return currency;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public void setMembers(List<Member> members) {
        this.members = members;
    }

    public void setBudget(Integer budget) {
        this.budget = budget;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }
}
