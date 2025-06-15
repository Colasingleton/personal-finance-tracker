package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.entity.Budget;
import com.financetracker.expense_tracker.entity.Category;
import com.financetracker.expense_tracker.entity.Expense;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.repository.BudgetRepository;
import com.financetracker.expense_tracker.repository.CategoryRepository;
import com.financetracker.expense_tracker.repository.ExpenseRepository;
import com.financetracker.expense_tracker.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseAnalyticsServiceTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @InjectMocks
    private ExpenseAnalyticsService analyticsService;

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private BudgetService budgetService;

    @InjectMocks
    private ExpenseService expenseService;

    @InjectMocks
    private UserService userService;

    @InjectMocks
    private CategoryService categoryService;

    // ExpenseAnalyticsService test
    @Test
    void testGetTotalSpendingForMonth_WithExpenses() {

        Long userId = 1L;
        Integer month = 6;
        Integer year = 2025;
        BigDecimal expectedTotal = new BigDecimal("150.00");

        when(expenseRepository.findTotalMonthlySpending(userId, month, year))
                .thenReturn(expectedTotal);

        BigDecimal result = analyticsService.getTotalSpendingForMonth(userId, month, year);

        assertEquals(expectedTotal, result);
        verify(expenseRepository).findTotalMonthlySpending(userId, month, year);
    }

    //BudgetService test

    @Test
    void testGetBudgetsByUserId(){
        Long userId = 1L;
        Budget budget1 = new Budget();
        Budget budget2 = new Budget();
        List<Budget> expectedBudgets = Arrays.asList(budget1, budget2);

        when(budgetRepository.findByUserIdOrderByBudgetYearDescBudgetMonthDesc(userId))
                .thenReturn(expectedBudgets);

        List<Budget> result = budgetService.getBudgetsByUserId(userId);

        assertEquals(2, result.size());
        assertEquals(expectedBudgets, result);
        verify(budgetRepository).findByUserIdOrderByBudgetYearDescBudgetMonthDesc(userId);
    }

    //ExpenseService test
    @Test
    void testSaveExpense() {
        Expense expense = new Expense();
        expense.setAmount(new BigDecimal("50.00"));
        expense.setDescription("test expense");

        when(expenseRepository.save(expense)).thenReturn(expense);

        Expense result = expenseService.saveExpense(expense);

        assertEquals(expense, result);
        assertEquals(new BigDecimal("50.00"), result.getAmount());
        assertEquals("test expense", result.getDescription());
        verify(expenseRepository).save(expense);
    }

    //UserService test
    @Test
    void testFindByUsername_UserExists() {
        String username = "testuser";
        User expectedUser = new User();
        expectedUser.setUsername(username);
        expectedUser.setEmail("test@exmaple.com");

        when(userRepository.findByUsername(username)).thenReturn(Optional.of(expectedUser));

        Optional<User> result = userService.findByUsername(username);

        assertTrue(result.isPresent());
        assertEquals(username, result.get().getUsername());
        assertEquals("test@exmaple.com", result.get().getEmail());
        verify(userRepository).findByUsername(username);
    }

    //CategoryService test

    @Test
    void testGetAllCategories() {
        Category food = new Category("Food","#FF6B6B", "fa-utensils");
        Category transport  = new Category("Transportation","#4ECDC4", "fa-car");
        List<Category> expectedCategories = Arrays.asList(food, transport);

        when(categoryRepository.findAll()).thenReturn(expectedCategories);
        List<Category> result = categoryService.getAllCategories();

        assertEquals(2, result.size());
        assertEquals("Food", result.get(0).getName());
        assertEquals("Transportation", result.get(1).getName());
        assertEquals("#FF6B6B", result.get(0).getColorCode());
        verify(categoryRepository).findAll();
    }

}
