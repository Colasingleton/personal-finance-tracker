package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.entity.Expense;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.service.CategoryService;
import com.financetracker.expense_tracker.service.ExpenseService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {

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
    public String addExpense(@Valid @ModelAttribute Expense expense,
                             BindingResult result, Model model) {
        if(result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "expenses/add";
        }

        User user = new User();
        user.setId(1L);
        expense.setUser(user);

        expenseService.saveExpense(expense);
        return "redirect:/expenses";
     }

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
