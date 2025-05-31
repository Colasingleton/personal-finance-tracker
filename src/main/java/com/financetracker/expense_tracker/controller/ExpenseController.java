package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.entity.Category;
import com.financetracker.expense_tracker.entity.Expense;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.repository.UserRepository;
import com.financetracker.expense_tracker.service.CategoryService;
import com.financetracker.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping
    public String listExpenses(Model model) {

        List<Expense> expenses = expenseService.getExpensesByUserId(1L);
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
                             Model model) {

        System.out.println("DEBUG: Starting addExpense method");
        System.out.println("DEBUG: Amount: " + amount);
        System.out.println("DEBUG: Category ID: " + categoryId);
        System.out.println("DEBUG: Date: " + expenseDate);
        System.out.println("DEBUG: Description: " + description);

        Expense expense = new Expense();
        expense.setAmount(amount);
        expense.setExpenseDate(expenseDate);
        expense.setDescription(description);

        Category category = categoryService.getCategoryById(categoryId).orElse(null);
        System.out.println("DEBUG: Found category: " + (category != null ? category.getName() : "NULL"));
        expense.setCategory(category);

        User user = userRepository.findById(1L).orElse(null);
        System.out.println("DEBUG: Found user: " + (user != null ? user.getUsername() : "NULL"));
        expense.setUser(user);

        System.out.println("DEBUG: About to save expense");
        try {
            Expense savedExpense = expenseService.saveExpense(expense);
            System.out.println("DEBUG: Saved expense with ID: " + savedExpense.getId());
        } catch (Exception e) {
            System.out.println("DEBUG: Error saving expense: " + e.getMessage());
            e.printStackTrace();
            return "expenses/add";
        }

        System.out.println("DEBUG: Redirecting to /expenses");
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
    public String showEditExpenseForm(@PathVariable Long id, Model model) {
        Expense expense = expenseService.getExpenseById(id).orElse(null);
        if(expense == null) {
            return "redirect:/expenses";
        }

        model.addAttribute("expense", expense);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "expenses/edit";
     }

     @PostMapping("/edit/{id}")
    public String editExpense(@PathVariable Long id,
                              @Valid @ModelAttribute Expense expense,
                              BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "expenses/edit";
        }

        expense.setId(id);
        Expense existingExpense = expenseService.getExpenseById(id).orElse(null);
        if(existingExpense == null) {
            expense.setUser(existingExpense.getUser());
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
