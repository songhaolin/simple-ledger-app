package com.ledger.config;

import com.ledger.model.Category;
import com.ledger.repository.CategoryRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试数据初始化
 */
@Configuration
public class TestDataInitializer {

    @Bean
    public CommandLineRunner initTestData(CategoryRepository categoryRepository) {
        return args -> {
            // 清空现有分类数据
            categoryRepository.deleteAll();

            // 初始化预设分类
            System.out.println("开始初始化测试分类数据...");

            // 支出分类
            Category cat1 = new Category();
            cat1.setId("cat_001");
            cat1.setType("expense");
            cat1.setName("餐饮");
            cat1.setIcon("🍚");
            cat1.setColor("#FF6B6B");
            cat1.setSortOrder(1);
            cat1.setIsDefault(true);
            categoryRepository.save(cat1);

            Category cat2 = new Category();
            cat2.setId("cat_002");
            cat2.setType("expense");
            cat2.setName("交通");
            cat2.setIcon("🚗");
            cat2.setColor("#4ECDC4");
            cat2.setSortOrder(2);
            cat2.setIsDefault(true);
            categoryRepository.save(cat2);

            Category cat3 = new Category();
            cat3.setId("cat_003");
            cat3.setType("expense");
            cat3.setName("购物");
            cat3.setIcon("🛍️");
            cat3.setColor("#95E1D3");
            cat3.setSortOrder(3);
            cat3.setIsDefault(true);
            categoryRepository.save(cat3);

            Category cat4 = new Category();
            cat4.setId("cat_004");
            cat4.setType("expense");
            cat4.setName("娱乐");
            cat4.setIcon("🎮");
            cat4.setColor("#F38181");
            cat4.setSortOrder(4);
            cat4.setIsDefault(true);
            categoryRepository.save(cat4);

            // 收入分类
            Category inc1 = new Category();
            inc1.setId("cat_101");
            inc1.setType("income");
            inc1.setName("工资");
            inc1.setIcon("💰");
            inc1.setColor("#A8E6CF");
            inc1.setSortOrder(1);
            inc1.setIsDefault(true);
            categoryRepository.save(inc1);

            Category inc2 = new Category();
            inc2.setId("cat_102");
            inc2.setType("income");
            inc2.setName("奖金");
            inc2.setIcon("🎁");
            inc2.setColor("#DCEDC1");
            inc2.setSortOrder(2);
            inc2.setIsDefault(true);
            categoryRepository.save(inc2);

            Category inc3 = new Category();
            inc3.setId("cat_103");
            inc3.setType("income");
            inc3.setName("理财");
            inc3.setIcon("📈");
            inc3.setColor("#FFD3B6");
            inc3.setSortOrder(3);
            inc3.setIsDefault(true);
            categoryRepository.save(inc3);

            Category inc4 = new Category();
            inc4.setId("cat_104");
            inc4.setType("income");
            inc4.setName("其他");
            inc4.setIcon("💵");
            inc4.setColor("#FFAAA5");
            inc4.setSortOrder(4);
            inc4.setIsDefault(true);
            categoryRepository.save(inc4);

            System.out.println("测试分类数据初始化完成！共 " + categoryRepository.count() + " 条");
        };
    }
}
