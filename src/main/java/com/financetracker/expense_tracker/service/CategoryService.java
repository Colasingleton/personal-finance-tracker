package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.entity.Category;
import com.financetracker.expense_tracker.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Optional<Category> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }

    public void initializeDefaultCategories() {
        if(categoryRepository.count() == 0) {
            categoryRepository.save(new Category("Food", "#FF6B6B", "fa-utensils"));
            categoryRepository.save(new Category("Transportation", "#4ECDC4","fa-car" ));
            categoryRepository.save(new Category("Entertainment", "#45B7D1", "fa-film"));
            categoryRepository.save(new Category("Bills", "F9CA24", "fa-file-invoice-dollar"));
            categoryRepository.save(new Category("Healthcare", "#F0932B", "fa-heartbeat"));
            categoryRepository.save(new Category("Shopping", "#EB4D4B", "fa-shopping-bag"));
            categoryRepository.save(new Category("Other", "#6C5CE7", "fa-question"));
        }
    }
}
