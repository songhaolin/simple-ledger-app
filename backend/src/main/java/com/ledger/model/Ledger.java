package com.ledger.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

/**
 * 账本实体
 */
@Data
@Document(collection = "ledgers")
public class Ledger {

    @Id
    private String id;           // 账本ID

    private String name;           // 账本名称

    private String type;           // 类型: "personal" | "family"

    private String ownerId;       // 创建者ID（关联users）

    private java.util.List<Member> members;  // 成员列表

    private Integer budget;        // 总预算（可选）

    private String currency;       // 货币（默认"CNY"）

    private Date createdAt;       // 创建时间

    private Date updatedAt;        // 更新时间

    /**
     * 成员信息
     */
    @Data
    public static class Member {
        private String userId;        // 用户ID
        private String role;           // 角色: "owner" | "member" | "viewer"
        private Date joinedAt;       // 加入时间
    }
}
