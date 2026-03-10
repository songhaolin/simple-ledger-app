package com.ledger.service;

import com.ledger.model.Category;
import com.ledger.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 分类服务
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    /**
     * 获取分类列表
     * 
     * @param type 类型（income/expense），可选
     * @return 分类列表
     */
    public List<Category> getCategories(String type) {
        List<Category> categories;

        if (type != null && (type.equals("income") || type.equals("expense"))) {
            // 根据类型查询
            categories = categoryRepository.findByType(type);
        } else {
            // 查询所有分类
            categories = categoryRepository.findAll();
        }

        // 按排序字段排序
        return categories.stream()
                .sorted(Comparator.comparing(Category::getSortOrder))
                .collect(Collectors.toList());
    }
}
