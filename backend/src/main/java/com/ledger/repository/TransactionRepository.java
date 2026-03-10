package com.ledger.repository;

import com.ledger.model.Transaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 账单仓库
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * 根据账本ID和时间范围查询账单
     */
    Page<Transaction> findByLedgerIdAndDateBetweenOrderByDateDesc(
            String ledgerId,
            Date startDate,
            Date endDate,
            Pageable pageable
    );

    /**
     * 根据账本ID查询所有账单
     */
    List<Transaction> findByLedgerId(String ledgerId);

    /**
     * 根据分类ID和时间范围查询
     */
    Page<Transaction> findByCategoryIdAndDateBetweenOrderByDateDesc(
            String categoryId,
            Date startDate,
            Date endDate,
            Pageable pageable
    );

    /**
     * 根据用户ID和时间范围查询
     */
    Page<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(
            String userId,
            Date startDate,
            Date endDate,
            Pageable pageable
    );
}
