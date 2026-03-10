package com.ledger.repository;

import com.ledger.model.Ledger;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 账本仓库
 */
@Repository
public interface LedgerRepository extends MongoRepository<Ledger, String> {

    /**
     * 根据所有者查询账本
     */
    List<Ledger> findByOwnerId(String ownerId);

    /**
     * 根据类型查询账本
     */
    List<Ledger> findByType(String type);

    /**
     * 查询用户所有账本（创建者 + 成员）
     */
    List<Ledger> findByOwnerIdOrMembersUserId(String ownerId, String userId);
}
