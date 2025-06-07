package com.financetracker.expense_tracker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "forecasts")
public class Forecast {

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

    @NotNull(message = "Predicted amount is required")
    @DecimalMin(value = "0.01", message = "Predicted amount must be greater than 0")
    @Column(name = "predicted_amount" ,nullable = false, precision = 10, scale = 2)
    private BigDecimal predictedAmount;


    @NotNull(message = " Forecast month is required")
    @Column(name = "forecast_month" ,nullable = false)
    private Integer forecastMonth;

    @NotNull(message = "Forecast year is required")
    @Column(name = "forecast_year" ,nullable = false)
    private Integer forecastYear;

    @Column(name = "algorithm_used", length = 50)
    private String algorithmUsed = "moving_average";

    @Column(name = "confidence_score" ,precision = 5, scale = 2)
    private BigDecimal confidenceScore;

    @Column(name = "based_on_months")
    private Integer basedOnMonths = 3;

    @Column(name = "actual_amount", precision = 10, scale = 2)
    private BigDecimal actualAmount;

    @Column(name = "created_date")
    private LocalDateTime createdDate = LocalDateTime.now();

    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    public Forecast() {}

    public Forecast(User user, Category category, BigDecimal predictedAmount, Integer forecastMonth, Integer forecastYear, String algorithmUsed) {
        this.user = user;
        this.category = category;
        this.predictedAmount = predictedAmount;
        this.forecastMonth = forecastMonth;
        this.forecastYear = forecastYear;
        this.algorithmUsed = algorithmUsed;
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

    public BigDecimal getPredictedAmount() {
        return predictedAmount;
    }

    public void setPredictedAmount(BigDecimal predictedAmount) {
        this.predictedAmount = predictedAmount;
    }

    public Integer getForecastMonth() {
        return forecastMonth;
    }

    public void setForecastMonth(Integer forecastMonth) {
        this.forecastMonth = forecastMonth;
    }

    public Integer getForecastYear() {
        return forecastYear;
    }

    public void setForecastYear(Integer forecastYear) {
        this.forecastYear = forecastYear;
    }

    public String getAlgorithmUsed() {
        return algorithmUsed;
    }

    public void setAlgorithmUsed(String algorithmUsed) {
        this.algorithmUsed = algorithmUsed;
    }

    public BigDecimal getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(BigDecimal confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public Integer getBasedOnMonths() {
        return basedOnMonths;
    }

    public void setBasedOnMonths(Integer basedOnMonths) {
        this.basedOnMonths = basedOnMonths;
    }

    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public boolean isAccurate() {
        if(actualAmount == null || predictedAmount == null){
            return false;
        }

        BigDecimal difference = actualAmount.subtract(predictedAmount).abs();
        BigDecimal threshold = actualAmount.multiply(BigDecimal.valueOf(0.20));
        return difference.compareTo(threshold) <= 0;
    }

    public BigDecimal getAccuracyPercentage() {
        if(actualAmount == null || predictedAmount == null){
            return null;
        }
        if(actualAmount.compareTo(BigDecimal.ZERO) == 0){
            return predictedAmount.compareTo(BigDecimal.ZERO) == 0 ?
                    BigDecimal.valueOf(100) : BigDecimal.ZERO;
        }
        BigDecimal difference = actualAmount.subtract(predictedAmount).abs();
        BigDecimal accuracy = BigDecimal.valueOf(100).subtract(
                difference.divide(actualAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
        );
        return accuracy.max(BigDecimal.ZERO);
    }
}
