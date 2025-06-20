package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.entity.Category;
import com.financetracker.expense_tracker.entity.Expense;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.repository.UserRepository;
import com.financetracker.expense_tracker.service.CategoryService;
import com.financetracker.expense_tracker.service.ExpenseAnalyticsService;
import com.financetracker.expense_tracker.service.ExpenseService;
import com.financetracker.expense_tracker.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenseService expenseService;

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
    */

    @GetMapping
    public String listExpenses(Model model,
                               @RequestParam(value = "search", required = false) String searchTerm,
                               @RequestParam(value = "startDate", required = false) String startDate,
                               @RequestParam(value = "endDate", required = false) String endDate,
                               Authentication authentication) {
        List<Expense> expenses;



        User user = userService.findByUsername(authentication.getName()).orElse(null);
        if (user == null) return "redirect:/login";
        Long userId = user.getId();

        //prevents null errors
        model.addAttribute("searchResults", false);
        model.addAttribute("dateRangeResults", false);


        if(searchTerm != null && !searchTerm.trim().isEmpty()) {
            expenses = expenseService.searchExpenses(userId, searchTerm);
            model.addAttribute("searchTerm", searchTerm);
            model.addAttribute("searchResults", true);
        } else if (startDate != null && endDate != null &&
                !startDate.isEmpty() && !endDate.isEmpty()) {
            try {
                LocalDate start = LocalDate.parse(startDate);
                LocalDate end = LocalDate.parse(endDate);
                expenses = expenseService.getExpensesByDateRange(userId, start, end);
                model.addAttribute("startDate", startDate);
                model.addAttribute("endDate", endDate);
                model.addAttribute("dateRangeResults", true);
            } catch (Exception e) {
                expenses = expenseService.getExpensesByUserId(userId);
                model.addAttribute("dateError", "invalid date format");
            }

        }
        else {
            expenses = expenseService.getExpensesByUserId(userId);
        }


        model.addAttribute("expenses", expenses);
        return "expenses/list";
    }

    @GetMapping("/add")
    public String showAddExpenseForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "expenses/add";
    }

    @PostMapping("/add")
    public String addExpense(@RequestParam("amount")BigDecimal amount,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam("expenseDate") LocalDate expenseDate,
                             @RequestParam(value = "description", required = false) String description,
                             Authentication authentication,
                             Model model) {


        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDate);
        expense.setDescription(description);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        expense.setCategory(category);

        User user = userService.findByUsername(authentication.getName()).orElse(null);
        expense.setUser(user);


        try {
            Expense savedExpense = expenseService.saveExpense(expense);
        } catch (Exception e) {
            e.printStackTrace();
            return "expenses/add";
        }

        return "redirect:/expenses";
    }
/*
    @PostMapping("/add")
    public String addExpense(@RequestParam("amount")BigDecimal amount,
                             @RequestParam("category.id") Long categoryId,
                             @RequestParam("expenseDate") LocalDate expenseDate,
                             @RequestParam(value = "description", required = false) String description,
                             Model model) {
        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDate);
        expense.setDescription(description);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        expense.setCategory(category);


        User user = userRepository.findById(1L).orElse(null);
        if (user != null) {
            model.addAttribute("error", "User not found");
            model.addAttribute("expense", expense);
            model.addAttribute("categories", categoryService.getAllCategories());
            return "expenses/add";
        }
        expense.setUser(user);
        expenseService.saveExpense(expense);
        return "redirect:/expenses";

    }
*/
     @GetMapping("/edit/{id}")
    public String showEditExpenseForm(@PathVariable Long id, Model model, Authentication authentication) {

         try{
             User user = userService.findByUsername(authentication.getName()).orElse(null);
             if (user == null) return "redirect:/login";


             Expense expense = expenseService.getExpenseById(id).orElse(null);
             if(expense == null) {
                 System.out.println("expense not found");
                 return "redirect:/expenses";
             }

             if(!expense.getUser().getId().equals(user.getId())) {
                 return "redirect:/expenses";
             }

             model.addAttribute("expense", expense);
             model.addAttribute("categories", categoryService.getAllCategories());
             return "expenses/edit";
         } catch (Exception e) {
             e.printStackTrace();
             return "redirect:/expenses";
         }
    }


     @PostMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id,
                              @ModelAttribute Expense expense,
                              BindingResult result, Model model, Authentication authentication) {

         expense.setId(id);
         Expense existingExpense = expenseService.getExpenseById(id).orElse(null);
         if(existingExpense != null) {
             expense.setUser(existingExpense.getUser());
         }

         if(result.hasErrors()) {
            System.out.println(result.getAllErrors());
            model.addAttribute("categories", categoryService.getAllCategories());
            return "expenses/edit";
        }



        expenseService.saveExpense(expense);
        return "redirect:/expenses";
     }

     @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, Model model) {
        expenseService.deleteExpense(id);
        return "redirect:/expenses";
     }
}
