package com.ledger.repository;

import com.ledger.model.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * 预算仓库
 */
@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    /**
     * 查找账本的所有预算
     */
    java.util.List<Budget> findByLedgerId(String ledgerId);

    /**
     * 查找账本中某个分类的预算
     */
    Budget findByLedgerIdAndCategoryId(String ledgerId, String categoryId);

    /**
     * 删除账本的所有预算
     */
    void deleteByLedgerId(String ledgerId);
}
