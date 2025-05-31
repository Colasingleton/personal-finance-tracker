package com.financetracker.expense_tracker.repository;

import com.financetracker.expense_tracker.entity.Budget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BudgetRepository extends JpaRepository<Budget, Long> {

    List<Budget> findByUserId(Long userId);

    List<Budget> findByUserIdAndBudgetMonthAndBudgetYear(
            Long userId, Integer budgetMonth, Integer budgetYear);

    Optional<Budget> findByUserIdAndCategoryIdAndBudgetMonthAndBudgetYear(
            Long userId, Long categoryId, Integer budgetMonth, Integer budgetYear);

    List<Budget> findByUserIdOrderByBudgetYearDescBudgetMonthDesc(Long userId);
}
