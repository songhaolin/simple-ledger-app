package com.ledger.repository;

import com.ledger.model.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 分类仓库
 */
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    /**
     * 根据类型查询分类
     */
    List<Category> findByType(String type);

    /**
     * 根据类型和是否预设查询
     */
    List<Category> findByTypeAndIsDefaultOrderBySortOrderAsc(String type, Boolean isDefault);

    /**
     * 查询所有分类（按类型和排序）
     */
    List<Category> findAllByOrderByTypeAscSortOrderAsc();
}
