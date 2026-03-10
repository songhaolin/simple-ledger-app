package com.ledger.controller;

import com.ledger.model.Category;
import com.ledger.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequiredArgsConstructor
@RequestMapping
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 获取分类列表
     * 
     * @param type 类型（income/expense），可选
     * @return 分类列表
     */
    @GetMapping
    public Response<List<Category>> getCategories(
            @RequestParam(required = false) String type) {
        List<Category> categories = categoryService.getCategories(type);
        return Response.success(categories);
    }
}
