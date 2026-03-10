package com.ledger.controller;

import com.ledger.model.Category;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 分类控制器测试 - TDD 示例
 * 
 * 测试目标：GET /api/v1/categories
 * 场景：
 * 1. 获取所有支出分类
 * 2. 获取所有收入分类
 * 3. 验证返回格式
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    /**
     * 测试1：获取所有支出分类
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - data 包含预设分类（餐饮、交通、购物等）
     */
    @Test
    public void shouldReturnAllExpenseCategories() throws Exception {
        mockMvc.perform(get("/categories?type=expense")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].name", hasItems("餐饮", "交通", "购物", "娱乐")))
                .andExpect(jsonPath("$.data[?(@.name=='餐饮')].icon", hasItem("🍚")))
                .andExpect(jsonPath("$.data[?(@.name=='交通')].icon", hasItem("🚗")))
                .andExpect(jsonPath("$.data[?(@.name=='餐饮')].type", hasItem("expense")))
                .andExpect(jsonPath("$.data[?(@.name=='餐饮')].isDefault", hasItem(true)));
    }

    /**
     * 测试2：获取所有收入分类
     * 
     * 预期：
     * - 状态码 200
     * - 返回 success: true
     * - data 包含收入分类（工资、奖金、理财、其他）
     */
    @Test
    public void shouldReturnAllIncomeCategories() throws Exception {
        mockMvc.perform(get("/categories?type=income")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[*].name", hasItems("工资", "奖金", "理财", "其他")))
                .andExpect(jsonPath("$.data[?(@.name=='工资')].icon", hasItem("💰")))
                .andExpect(jsonPath("$.data[?(@.name=='工资')].type", hasItem("income")));
    }

    /**
     * 测试3：不传type参数，返回所有分类
     * 
     * 预期：
     * - 状态码 200
     * - 返回所有分类（支出+收入）
     */
    @Test
    public void shouldReturnAllCategoriesWhenTypeNotProvided() throws Exception {
        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data", hasSize(greaterThan(0))));
    }

    /**
     * 测试4：验证响应格式
     * 
     * 预期：
     * - 包含必需字段：id, name, icon, color, type, isDefault
     */
    @Test
    public void shouldReturnCorrectFormat() throws Exception {
        mockMvc.perform(get("/categories?type=expense")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data[0]").exists())
                .andExpect(jsonPath("$.data[0].id").exists())
                .andExpect(jsonPath("$.data[0].name").exists())
                .andExpect(jsonPath("$.data[0].icon").exists())
                .andExpect(jsonPath("$.data[0].color").exists())
                .andExpect(jsonPath("$.data[0].type").exists())
                .andExpect(jsonPath("$.data[0].isDefault").exists());
    }
}
