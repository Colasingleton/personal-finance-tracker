package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.entity.Budget;
import com.financetracker.expense_tracker.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BudgetService {

    @Autowired
    private BudgetRepository budgetRepository;

    public List<Budget> getAllBudgets(){
        return budgetRepository.findAll();
    }

    public List<Budget> getBudgetsByUserId(Long userId){
        return budgetRepository.findByUserIdOrderByBudgetYearDescBudgetMonthDesc(userId);
    }

    public List<Budget> getBudgetsByUserAndMonth(Long userId, Integer month, Integer year){
        return budgetRepository.findByUserIdAndBudgetMonthAndBudgetYear(userId, month, year);
    }

    public Optional<Budget> getBudgetByUserCategoryAndMonth(
            Long userId, Long categoryId, Integer month, Integer year){
        return budgetRepository.findByUserIdAndCategoryIdAndBudgetMonthAndBudgetYear(
                userId, categoryId, month, year);
    }

    public Optional<Budget> getBudgetById(Long id){
        return budgetRepository.findById(id);
    }

    public Budget saveBudget(Budget budget){
       return budgetRepository.save(budget);
    }

    public void  deleteBudget(Long id){
        budgetRepository.deleteById(id);
    }

    public BigDecimal getTotalBudgetForMonth(Long userId, Integer month, Integer year){
        List<Budget> budgets = getBudgetsByUserAndMonth(userId, month, year);
        return budgets.stream()
                .map(Budget::getMonthlyAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public List<Budget> getCurrentMonthBudgets(Long userId) {
        LocalDate now = LocalDate.now();
        return getBudgetsByUserAndMonth(userId, now.getMonthValue(), now.getYear());
    }
}
