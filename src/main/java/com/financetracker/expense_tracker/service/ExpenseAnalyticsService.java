package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.dto.BudgetVsActual;
import com.financetracker.expense_tracker.dto.MonthlySpending;
import com.financetracker.expense_tracker.entity.Budget;
import com.financetracker.expense_tracker.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ExpenseAnalyticsService {

    @Autowired
    private ExpenseRepository expenseRepository;

    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryService categoryService;

    public List<MonthlySpending> getMonthlySpendingByCategory(Long userId, Integer month, Integer year) {
        List<Object[]> results = expenseRepository.findMonthlySpendingByCategory(userId, month, year);

        return results.stream().map(row -> new MonthlySpending(
                (Long) row[0],
                (String) row[1],
                (String) row[2],
                (String) row[3],
                (Integer) row[4],
                (Integer) row[5],
                (BigDecimal) row[6],
                (Long) row[7]
        )).collect(Collectors.toList());

    }

    public BigDecimal getTotalSpendingForMonth(Long userId, Integer month, Integer year) {
        BigDecimal total = expenseRepository.findTotalMonthlySpending(userId, month, year);
        return total != null ? total : BigDecimal.ZERO;
    }

    public List<MonthlySpending> getSpendingTrends(Long userId, Integer numberOfMonths) {
        LocalDate startDate = LocalDate.now().minusMonths(numberOfMonths);
        List<Object[]> results = expenseRepository.findSpendingTrends(userId, startDate);

        return results.stream().map(row-> new MonthlySpending(
                (Integer) row[0], //month
                (Integer) row[1], //year
                (BigDecimal) row[2] //total amount
        )).collect(Collectors.toList());
    }

    //get specific category for month
    public BigDecimal getCategorySpendingForMonth(Long userId, Long categoryId,Integer month, Integer year) {
        BigDecimal amount = expenseRepository.findMonthlySpendingByUserAndCategory(userId, categoryId, month, year);
        return amount != null ? amount : BigDecimal.ZERO;
    }

    //get category trends for forecasting
    public List<MonthlySpending> getCategorySpendingTrends(Long userId, Long categoryId, Integer numberOfMonths) {
        LocalDate startDate = LocalDate.now().minusMonths(numberOfMonths);
        List<Object[]> results = expenseRepository.findCategorySpendingTrends(userId, categoryId, startDate);
        List<MonthlySpending> trendList = new ArrayList<>();

        return results.stream().map(row-> new MonthlySpending(
                (Integer) row[0], //month
                (Integer) row[1], //year
                (BigDecimal) row[2] //total amount
        )).collect(Collectors.toList());
    }

    //compare budget vs actual for current month

    public List<BudgetVsActual> getCurrentMonthBudgetVsActual(Long userId) {
        LocalDate now = LocalDate.now();
        return getBudgetVsActual(userId, now.getMonthValue(), now.getYear());
    }

    public List<BudgetVsActual> getBudgetVsActual(Long userId, Integer month, Integer year) {
       List<Budget> budgets = budgetService.getBudgetsByUserAndMonth(userId, month, year);
       List<BudgetVsActual> comparisons = new ArrayList<>();

       for(Budget budget : budgets) {
           BigDecimal actualSpending = getCategorySpendingForMonth(userId, budget.getCategory().getId(), month, year);

           BudgetVsActual comparison = new BudgetVsActual(
                   budget.getCategory().getId(),
                   budget.getCategory().getName(),
                   budget.getCategory().getColorCode(),
                   budget.getCategory().getIcon(),
                   month,
                   year,
                   budget.getMonthlyAmount(),
                   actualSpending
           );

           comparisons.add(comparison);
       }
       return comparisons;
    }

    //getting dashboard for current month
    public DashboardSummary getCurrentMonthSummary(Long userId) {
        LocalDate now = LocalDate.now();
        Integer currentMonth = now.getMonthValue();
        Integer currentYear = now.getYear();

        //total spending this month
        BigDecimal totalSpending = getTotalSpendingForMonth(userId, currentMonth, currentYear);

        //total budget this month
        BigDecimal totalBudget = budgetService.getTotalBudgetForMonth(userId, currentMonth, currentYear);

        //spending category
        List<MonthlySpending> categoryBreakdown = getMonthlySpendingByCategory(userId, currentMonth, currentYear);

        //budget comparisons
        List<BudgetVsActual> budgetComparisons = getCurrentMonthBudgetVsActual(userId);

        return new DashboardSummary(totalSpending, totalBudget, categoryBreakdown, budgetComparisons);
    }

    public static class DashboardSummary {
        private BigDecimal totalSpending;
        private BigDecimal totalBudget;
        private List<MonthlySpending> categoryBreakdown;
        private List<BudgetVsActual> budgetComparisons;

        public DashboardSummary(BigDecimal totalSpending, BigDecimal totalBudget,
                                List<MonthlySpending> categoryBreakdown,
                                List<BudgetVsActual> budgetComparisons) {
            this.totalSpending = totalSpending;
            this.totalBudget = totalBudget;
            this.categoryBreakdown = categoryBreakdown;
            this.budgetComparisons = budgetComparisons;
        }

        public BigDecimal getTotalSpending() {
            return totalSpending;
        }

        public BigDecimal getTotalBudget() {
            return totalBudget;
        }

        public List<MonthlySpending> getCategoryBreakdown() {
            return categoryBreakdown;
        }

        public List<BudgetVsActual> getBudgetComparisons() {
            return budgetComparisons;
        }

        public BigDecimal getRemainingBudget() {
            return totalBudget.subtract(totalSpending);
        }

        public double getBudgetUsedPercentage() {
            if(totalBudget.compareTo(BigDecimal.ZERO) == 0) return 0.0;
            return totalSpending.divide(totalBudget, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100)).doubleValue();
        }
    }
}
