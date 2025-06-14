package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.entity.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class ReportService {

    @Autowired private ExpenseService expenseService;
    @Autowired private ExpenseAnalyticsService analyticsService;

    public byte[] generateExpenseReport(Long userId, LocalDate startDate, LocalDate endDate, String title) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(outputStream);

        writer.println("=".repeat(60));
        writer.println(title != null ? title  :"EXPENSE REPORT");
        writer.println("Generated: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss")));
        writer.println("Period: " + startDate + " to " + endDate);
        writer.println("=".repeat(60));

        List<Expense> expenses = expenseService.getExpensesByDateRange(userId, startDate, endDate);
        writer.printf("%-12s %-15s %-25s %-10s%n", "Date", "Category", "Description", "Amount");
        writer.println("-".repeat(60));

        double total = 0;
        for (Expense expense : expenses) {
            writer.printf("%-12s %-15s %-25s $%-9.2f%n",
                    expense.getExpenseDate(),
                    expense.getCategory().getName(),
                    expense.getDescription() != null ? expense.getDescription() : "N/A",
                    expense.getAmount().doubleValue());
            total += expense.getAmount().doubleValue();
        }
        writer.println("-".repeat(60));
        writer.printf("TOTAL: $%.2f%n" , total);
        writer.close();
        return outputStream.toByteArray();
    }
}
