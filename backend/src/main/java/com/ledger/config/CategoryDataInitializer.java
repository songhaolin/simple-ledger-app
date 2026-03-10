package com.ledger.config;

import com.ledger.model.Category;
import com.ledger.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 预设分类数据初始化
 */
@Slf4j
@Component
@Profile("init-data")  // 只在 init-data profile 时运行
@RequiredArgsConstructor
public class CategoryDataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        log.info("开始初始化预设分类数据...");

        // 检查是否已初始化
        if (categoryRepository.count() > 0) {
            log.info("分类数据已存在，跳过初始化");
            return;
        }

        // 创建支出分类
        List<Category> expenseCategories = createExpenseCategories();
        categoryRepository.saveAll(expenseCategories);

        // 创建收入分类
        List<Category> incomeCategories = createIncomeCategories();
        categoryRepository.saveAll(incomeCategories);

        log.info("预设分类数据初始化完成！共 {} 个分类", 
                expenseCategories.size() + incomeCategories.size());
    }

    /**
     * 创建支出分类
     */
    private List<Category> createExpenseCategories() {
        List<Category> categories = new ArrayList<>();

        // 餐饮
        Category food = new Category();
        food.setName("餐饮");
        food.setIcon("🍚");
        food.setColor("#FF6B6B");
        food.setType("expense");
        food.setIsDefault(true);
        food.setSortOrder(1);
        categories.add(food);

        // 交通
        Category transport = new Category();
        transport.setName("交通");
        transport.setIcon("🚗");
        transport.setColor("#4ECDC4");
        transport.setType("expense");
        transport.setIsDefault(true);
        transport.setSortOrder(2);
        categories.add(transport);

        // 购物
        Category shopping = new Category();
        shopping.setName("购物");
        shopping.setIcon("🛍️");
        shopping.setColor("#FFA502");
        shopping.setType("expense");
        shopping.setIsDefault(true);
        shopping.setSortOrder(3);
        categories.add(shopping);

        // 娱乐
        Category entertainment = new Category();
        entertainment.setName("娱乐");
        entertainment.setIcon("🎮");
        entertainment.setColor("#A55EEA");
        entertainment.setType("expense");
        entertainment.setIsDefault(true);
        entertainment.setSortOrder(4);
        categories.add(entertainment);

        // 居住
        Category housing = new Category();
        housing.setName("居住");
        housing.setIcon("🏠");
        housing.setColor("#F368E0");
        housing.setType("expense");
        housing.setIsDefault(true);
        housing.setSortOrder(5);
        categories.add(housing);

        // 医疗
        Category medical = new Category();
        medical.setName("医疗");
        medical.setIcon("💊");
        medical.setColor("#0ABDE3");
        medical.setType("expense");
        medical.setIsDefault(true);
        medical.setSortOrder(6);
        categories.add(medical);

        // 教育
        Category education = new Category();
        education.setName("教育");
        education.setIcon("📚");
        education.setColor("#10AC84");
        education.setType("expense");
        education.setIsDefault(true);
        education.setSortOrder(7);
        categories.add(education);

        // 其他
        Category other = new Category();
        other.setName("其他");
        other.setIcon("📦");
        other.setColor("#C44569");
        other.setType("expense");
        other.setIsDefault(true);
        other.setSortOrder(8);
        categories.add(other);

        return categories;
    }

    /**
     * 创建收入分类
     */
    private List<Category> createIncomeCategories() {
        List<Category> categories = new ArrayList<>();

        // 工资
        Category salary = new Category();
        salary.setName("工资");
        salary.setIcon("💰");
        salary.setColor("#10AC84");
        salary.setType("income");
        salary.setIsDefault(true);
        salary.setSortOrder(1);
        categories.add(salary);

        // 奖金
        Category bonus = new Category();
        bonus.setName("奖金");
        bonus.setIcon("🎁");
        bonus.setColor("#10AC84");
        bonus.setType("income");
        bonus.setIsDefault(true);
        bonus.setSortOrder(2);
        categories.add(bonus);

        // 理财
        Category investment = new Category();
        investment.setName("理财");
        investment.setIcon("📈");
        investment.setColor("#10AC84");
        investment.setType("income");
        investment.setIsDefault(true);
        investment.setSortOrder(3);
        categories.add(investment);

        // 其他
        Category other = new Category();
        other.setName("其他");
        other.setIcon("💵");
        other.setColor("#10AC84");
        other.setType("income");
        other.setIsDefault(true);
        other.setSortOrder(4);
        categories.add(other);

        return categories;
    }
}
