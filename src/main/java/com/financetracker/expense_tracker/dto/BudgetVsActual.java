package com.financetracker.expense_tracker.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BudgetVsActual {
    private Long categoryId;
    private String categoryName;
    private String categoryColor;
    private String categoryIcon;
    private Integer month;
    private Integer year;
    private BigDecimal budgetAmount;
    private BigDecimal actualAmount;
    private BigDecimal difference;
    private Double percentageUsed;

    public BudgetVsActual() {
    }

    public BudgetVsActual(Long categoryId, String categoryName,
                          String categoryColor, String categoryIcon,
                          Integer month, Integer year,
                          BigDecimal budgetAmount, BigDecimal actualAmount) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.categoryColor = categoryColor;
        this.categoryIcon = categoryIcon;
        this.month = month;
        this.year = year;
        this.budgetAmount = budgetAmount;
        this.actualAmount = actualAmount != null ? actualAmount : BigDecimal.ZERO;

        this.difference = this.budgetAmount.subtract(this.actualAmount);
        if(this.budgetAmount.compareTo(BigDecimal.ZERO) > 0){
            this.percentageUsed = this.actualAmount.divide(this.budgetAmount, 4,
                    RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100)).doubleValue();
        } else {
            this.percentageUsed = 0.0;
        }
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

    public BigDecimal getBudgetAmount() {
        return budgetAmount;
    }

    public void setBudgetAmount(BigDecimal budgetAmount) {
        this.budgetAmount = budgetAmount;
    }
    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public BigDecimal getDifference() {
        return difference;
    }

    public void setDifference(BigDecimal difference) {
        this.difference = difference;
    }

    public Double getPercentageUsed() {
        return percentageUsed;
    }

    public void setPercentageUsed(Double percentageUsed) {
        this.percentageUsed = percentageUsed;
    }

    public boolean isOverBudget(){
        return this.actualAmount.compareTo(budgetAmount) > 0;
    }

    public boolean isNearBudgetLimit(){
        return percentageUsed >= 80.0 && percentageUsed < 100.0;
    }

}
