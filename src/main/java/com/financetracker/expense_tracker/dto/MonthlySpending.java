package com.financetracker.expense_tracker.dto;

import java.math.BigDecimal;

public class MonthlySpending {
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private Integer month;
    private Integer year;
    private BigDecimal totalAmount;
    private Long expenseCount;

    public MonthlySpending() {
    }

    public MonthlySpending(Long categoryId, String categoryName,
                           String categoryColor, String categoryIcon,
                           Integer month, Integer year, BigDecimal totalAmount,
                           Long expenseCount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.month = month;
        this.year = year;
        this.expenseCount = expenseCount;
        this.totalAmount = totalAmount;
    }

    public MonthlySpending(Integer month, Integer year, BigDecimal totalAmount) {
        this.month = month;
        this.year = year;
        this.totalAmount = totalAmount;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryColor() {
        return categoryColor;
    }

    public void setCategoryColor(String categoryColor) {
        this.categoryColor = categoryColor;
    }

    public String getCategoryIcon() {
        return categoryIcon;
    }

    public void setCategoryIcon(String categoryIcon) {
        this.categoryIcon = categoryIcon;
    }

    public Integer getMonth() {
        return month;
    }

    public void setMonth(Integer month) {
        this.month = month;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Long getExpenseCount() {
        return expenseCount;
    }

    public void setExpenseCount(Long expenseCount) {
        this.expenseCount = expenseCount;
    }
}
