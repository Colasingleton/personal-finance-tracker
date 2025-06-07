package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.entity.Forecast;
import com.financetracker.expense_tracker.service.ExpenseAnalyticsService;
import com.financetracker.expense_tracker.service.ForecastingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
public class AuthController {

    @Autowired
    private ExpenseAnalyticsService analyticsService;

    @Autowired
    private ForecastingService forecastingService;


    @GetMapping("/login")
        public String login() {
        return "login";
        }


    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        try{
            ExpenseAnalyticsService.DashboardSummary summary = analyticsService.getCurrentMonthSummary(1L);
            List<Forecast> forecasts = forecastingService.generateForecastsForAllCategories(1L);

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
            model.addAttribute("analyticsError", "Analytics temporarily unavailable");
        }
            return "dashboard";
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/dashboard";
    }

}