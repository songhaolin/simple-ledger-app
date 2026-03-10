package com.ledger.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * 用户实体
 */
@Data
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
}
