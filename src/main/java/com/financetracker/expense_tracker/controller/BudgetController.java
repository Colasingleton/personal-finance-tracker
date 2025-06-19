package com.financetracker.expense_tracker.controller;


import com.financetracker.expense_tracker.dto.MonthlySpending;
import com.financetracker.expense_tracker.entity.*;
import com.financetracker.expense_tracker.repository.BudgetRepository;
import com.financetracker.expense_tracker.repository.ForecastRepository;
import com.financetracker.expense_tracker.repository.UserRepository;
import com.financetracker.expense_tracker.service.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;



@Controller
@RequestMapping("/budgets")
public class BudgetController {


    @Autowired
    private BudgetService budgetService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;
/*
    @GetMapping("/analytics-test")
    public String testAnalytics(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        ExpenseAnalyticsService.DashboardSummary summary = analyticsService.getCurrentMonthSummary(user.getId());
        model.addAttribute("summary", summary);
        model.addAttribute("categoryBreakdown", summary.getCategoryBreakdown());
        model.addAttribute("budgetComparisons", summary.getBudgetComparisons());
        return "analytics-test";
    }



    @GetMapping("/test-forecasting")
    public String testForecasting(Model model, Authentication authentication) {
        try {
            System.out.println("=== Testing forecasting service ===");

            User user = userService.findByUsername(authentication.getName()).orElse(null);
            if (user == null) return "redirect:/login";

            List<Forecast> forecasts = forecastingService.generateForecastsForAllCategories(user.getId());
            System.out.println("Generated " + forecasts.size() + " forecasts");

            ForecastingService.ForecastAccuracyReport report = forecastingService.getForecastAccuracyReport(user.getId());
            System.out.println("Accuracy report: " + report.getTotalForecasts() + " total forecasts");

            model.addAttribute("forecasts", forecasts);
            model.addAttribute("report", report);
            model.addAttribute("message", "Forecasting test completed - check console");
            return "analytics-test";
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("error", "Error: " + e.getMessage());
            return "analytics-test";
        }
    }
*/


    @GetMapping
    public String listBudgets(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        List<Budget> budgets = budgetService.getBudgetsByUserId(user.getId());
        model.addAttribute("budgets", budgets);

        LocalDate now = LocalDate.now();
        model.addAttribute("currentMonth", now.getMonthValue());
        model.addAttribute("currentYear", now.getYear());
        return "budgets/list";
    }

    @GetMapping("/add")
    public String showAddBudgetForm(Model model) {
        model.addAttribute("budget", new Budget());
        model.addAttribute("categories", categoryService.getAllCategories());

        LocalDate now = LocalDate.now();
        model.addAttribute("defaultMonth", now.getMonthValue());
        model.addAttribute("defaultYear", now.getYear());

        return "budgets/add";
    }

    @PostMapping("/add")
    public String addBudget(@RequestParam("monthlyAmount") BigDecimal monthlyAmount,
                            @RequestParam("category.id") Long categoryId,
                            @RequestParam("budgetMonth") Integer budgetMonth,
                            @RequestParam("budgetYear") Integer budgetYear,
                            Authentication authentication,
                            Model model) {
        User currentUser = userService.findByUsername(authentication.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }

        Optional<Budget> existingBudget = budgetService.getBudgetByUserCategoryAndMonth(
                currentUser.getId(), categoryId, budgetMonth, budgetYear);

        if (existingBudget.isPresent()) {
            model.addAttribute("error",
                    "A budget for this category already exists, please edit instead.");
            model.addAttribute("budget", new Budget());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("defaultMonth", budgetMonth);
            model.addAttribute("defaultYear", budgetYear);
            return "budgets/add";
        }
        Budget budget = new Budget();
        budget.setMonthlyAmount(monthlyAmount);
        budget.setBudgetMonth(budgetMonth);
        budget.setBudgetYear(budgetYear);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        if (category == null) {
            model.addAttribute("error", "Invalid category selected.");
            model.addAttribute("budget", budget);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "budgets/add";
        }
        budget.setCategory(category);
        budget.setUser(currentUser);

        try {
            budgetService.saveBudget(budget);
        } catch (Exception e) {
            model.addAttribute("error", "Error saving budget." + e.getMessage());
            model.addAttribute("budget", budget);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "budgets/add";
        }
        return "redirect:/budgets";
    }

    @GetMapping("/edit/{id}")
    public String showEditBudgetForm(@PathVariable Long id, Model model) {
        Budget budget = budgetService.getBudgetById(id).orElse(null);
        if (budget == null) {
            return "redirect:/budgets";
        }

        model.addAttribute("budget", budget);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "budgets/edit";
    }

    @PostMapping("/edit/{id}")
    public String editBudget(@PathVariable Long id,
                             @Valid @ModelAttribute Budget budget,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "budgets/edit";
        }
        Budget existingBudget = budgetService.getBudgetById(id).orElse(null);
        if (existingBudget == null) {
            return "redirect:/budgets";
        }

        budget.setId(id);
        budget.setUser(existingBudget.getUser());

        budgetService.saveBudget(budget);
        return "redirect:/budgets";
    }

    @GetMapping("/delete/{id}")
    public String deleteBudget(@PathVariable Long id) {
        Budget budget = budgetService.getBudgetById(id).orElse(null);
        if (budget != null) {
            budgetService.deleteBudget(id);
        }
        return "redirect:/budgets";
    }

    private User getAuthenticatedUser(Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) {
            throw new RuntimeException("User not authenticated");
        }
        return user;
    }

}
