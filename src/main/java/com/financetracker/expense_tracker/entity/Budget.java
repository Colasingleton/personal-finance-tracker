package com.financetracker.expense_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "budgets")
public class Budget {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotNull(message = "User is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @NotNull(message = "Monthly amount is required")
    @DecimalMin(value = "0.01", message = "Monthly amount must be greater than 0")
    @Column(name = "monthly_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal monthlyAmount;


    @NotNull(message = "Budget month is required")
    @Column(name = "budget_month", nullable = false)
    private Integer budgetMonth;


    @NotNull(message = "Budget year is required")
    @Column(name = "budget_year", nullable = false)
    private Integer budgetYear;


    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    public Budget() {
    }

    public Budget(User user, Category category, BigDecimal monthlyAmount, Integer budgetMonth, Integer budgetYear) {
        this.user = user;
        this.category = category;
        this.monthlyAmount = monthlyAmount;
        this.budgetMonth = budgetMonth;
        this.budgetYear = budgetYear;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public BigDecimal getMonthlyAmount() {
        return monthlyAmount;
    }

    public void setMonthlyAmount(BigDecimal monthlyAmount) {
        this.monthlyAmount = monthlyAmount;
    }

    public Integer getBudgetMonth() {
        return budgetMonth;
    }

    public void setBudgetMonth(Integer budgetMonth) {
        this.budgetMonth = budgetMonth;
    }

    public Integer getBudgetYear() {
        return budgetYear;
    }

    public void setBudgetYear(Integer budgetYear) {
        this.budgetYear = budgetYear;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }
}
