package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.dto.MonthlySpending;
import com.financetracker.expense_tracker.entity.Forecast;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Controller
public class DashboardController {
    @Autowired
    private ExpenseAnalyticsService analyticsService;

    @Autowired
    private UserService userService;

    @Autowired
    private ForecastingService forecastingService;

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        Long userId = user.getId();


        try{
            ExpenseAnalyticsService.DashboardSummary summary = analyticsService.getCurrentMonthSummary(userId);
            List<Forecast> forecasts = forecastingService.generateForecastsForAllCategories(userId);

            //for chart data
            List<MonthlySpending> spendingTrends = analyticsService.getSpendingTrends(userId, 6);

            //debugging

            categoryService.debugCategories();
            ExpenseAnalyticsService.DashboardSummary summary2 = analyticsService.getCurrentMonthSummary(userId);

            System.out.println("=== SPENDING TRENDS DEBUG ===");
            System.out.println("Spending trends size: " + spendingTrends.size());
            for (MonthlySpending trend : spendingTrends) {
                System.out.println("Month: " + trend.getMonth() + ", Year: " + trend.getYear() + ", Amount: " + trend.getTotalAmount());
            }
            //debugging

            model.addAttribute("spendingTrends", spendingTrends);


            BigDecimal totalPredicted = forecasts.stream()
                    .map(Forecast::getPredictedAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            model.addAttribute("totalPredicted", totalPredicted);

            LocalDate now = LocalDate.now();
            String currentMonthName = now.getMonth().name().substring(0,1) +
                    now.getMonth().name().substring(1).toLowerCase();

            model.addAttribute("summary", summary);
            model.addAttribute("forecast", forecasts);
            model.addAttribute("currentMonth", currentMonthName);
            model.addAttribute("currentYear", now.getYear());

            double budgetPercentageUsed = summary.getBudgetUsedPercentage();
            model.addAttribute("budgetPercentageUsed", budgetPercentageUsed);

            String budgetStatusColor = budgetPercentageUsed >= 90 ? "danger" :
                    budgetPercentageUsed >= 75 ? "warning" : "success";
            model.addAttribute("budgetStatusColor", budgetStatusColor);

        } catch (Exception e){
            System.err.println("Dashboard error: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("analyticsError", "Dashboard temporarily unavailable. Please try again later.");
            model.addAttribute("summary", null);
            model.addAttribute("forecast", new ArrayList<>());
            model.addAttribute("spendingTrends", new ArrayList<>());
        }
        return "dashboard";
    }
}
