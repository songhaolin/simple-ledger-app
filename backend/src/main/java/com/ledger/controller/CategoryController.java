package com.ledger.controller;

import com.ledger.model.Category;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类控制器
 */
@RestController
@RequestMapping("/categories")
public class CategoryController {

    /**
     * 获取分类列表
     * 
     * @param type 类型（income/expense），可选
     * @return 分类列表
     */
    @GetMapping
    public Response<List<Category>> getCategories(
            @RequestParam(required = false) String type) {
        // TODO: 实现分类查询逻辑
        return Response.success(List.of());
    }
}
