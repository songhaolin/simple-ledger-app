package com.ledger.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * 用户实体
 */
@Document(collection = "users")
public class User {

    @Id
    private String id;

    private String phone;        // 手机号（唯一）

    private String passwordHash;  // 密码哈希

    private String nickname;     // 昵称

    private String avatar;       // 头像URL

    private Date createdAt;      // 创建时间

    private Date updatedAt;      // 更新时间

    private Boolean isActive;    // 是否激活

    private Date lastLoginAt;   // 最后登录时间

    // Getters
    public String getId() {
        return id;
    }

    public String getPhone() {
        return phone;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getNickname() {
        return nickname;
    }

    public String getAvatar() {
        return avatar;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public Date getLastLoginAt() {
        return lastLoginAt;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public void setLastLoginAt(Date lastLoginAt) {
        this.lastLoginAt = lastLoginAt;
    }
}
