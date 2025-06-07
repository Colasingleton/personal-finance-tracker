package com.financetracker.expense_tracker.service;

import com.financetracker.expense_tracker.dto.MonthlySpending;
import com.financetracker.expense_tracker.entity.Category;
import com.financetracker.expense_tracker.entity.Forecast;
import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.repository.ForecastRepository;
import com.financetracker.expense_tracker.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ForecastingService {

    @Autowired
    private ForecastRepository forecastRepository;

    @Autowired
    private ExpenseAnalyticsService analyticsService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserRepository userRepository;

    public Forecast generateMovingAverageForecast(Long userId, Long categoryId) {
        LocalDate now = LocalDate.now();
        Integer nextMonth = now.getMonthValue() == 12 ? 1: now.getMonthValue() +1;
        Integer nextYear = now.getMonthValue() == 12 ? now.getYear() +1 : now.getYear();

        List<MonthlySpending> trends = analyticsService.getCategorySpendingTrends(userId, categoryId, 3);
        if(trends.size() < 2) {
            return null;
        }

        BigDecimal total = BigDecimal.ZERO;
        int monthsUsed = Math.min(3, trends.size());

        for(int i = 0; i < monthsUsed; i++) {
            total = total.add(trends.get(i).getTotalAmount());
        }

        BigDecimal predictedAmount = total.divide(BigDecimal.valueOf(monthsUsed), 2, RoundingMode.HALF_UP);
        BigDecimal confidenceScore = calculateConfidenceScore(trends, predictedAmount);

        User user = userRepository.findById(userId).orElse(null);
        Category category = categoryService.getCategoryById(categoryId).orElse(null);

        if(user == null || category == null) {
            return null;
        }

        Forecast forecast = new Forecast(user, category, predictedAmount,
                nextMonth, nextYear, "moving_average");
        forecast.setConfidenceScore(confidenceScore);
        forecast.setBasedOnMonths(monthsUsed);
        return forecastRepository.save(forecast);
    }

    //generate forecast for all categories
    public List<Forecast> generateForecastsForAllCategories(Long userId) {
        List<Category> categories = categoryService.getAllCategories();
        List<Forecast> forecasts = new ArrayList<>();

        for(Category category : categories) {
            BigDecimal recentSpending = analyticsService.getCategorySpendingForMonth(
                    userId, category.getId(), LocalDate.now().getMonthValue(),LocalDate.now().getYear());

            List<MonthlySpending> trends = analyticsService.getCategorySpendingTrends(userId, category.getId(), 3);

            if(!trends.isEmpty() && trends.stream().anyMatch(t->t.getTotalAmount().compareTo(BigDecimal.ZERO)>0)) {
                Forecast forecast = generateMovingAverageForecast(userId, category.getId());
                if(forecast != null) {
                    forecasts.add(forecast);
                }

            }
        }
        return forecasts;
    }

    //generate existing forecast or new
    public Forecast getForecastForNextMonth(Long userId, Long categoryId) {
        LocalDate now = LocalDate.now();
        Integer nextMonth = now.getMonthValue() == 12 ? 1: now.getMonthValue() +1;
        Integer nextYear = now.getMonthValue() == 12 ? now.getYear() +1 : now.getYear();

        //check if already exists
        Optional<Forecast> existingForecast = forecastRepository
                .findByUserIdAndCategoryIdAndForecastMonthAndForecastYear(
                        userId, categoryId, nextMonth, nextYear);

        if(existingForecast.isPresent()) {
            return existingForecast.get();
        }
        return generateMovingAverageForecast(userId, categoryId);
    }

    //calculate confidence score based on spending consistency
    private BigDecimal calculateConfidenceScore(List<MonthlySpending> trends, BigDecimal predictedAmount) {
        if(trends.size() < 2) {
            return BigDecimal.valueOf(50);
        }

        //calculate variance in spending
        BigDecimal sumOfSquaredDifferences = BigDecimal.ZERO;
        for(MonthlySpending trend : trends) {
            BigDecimal difference = trend.getTotalAmount().subtract(predictedAmount);
            sumOfSquaredDifferences = sumOfSquaredDifferences.add(difference.multiply(difference));
        }

        BigDecimal variance = sumOfSquaredDifferences.divide(BigDecimal.valueOf(trends.size()), 4, RoundingMode.HALF_UP);
        BigDecimal standardDeviation = BigDecimal.valueOf(Math.sqrt(variance.doubleValue()));

        //low standard deviation = higher confidence
        if(predictedAmount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.valueOf(90); //high confidence for zero spending
        }

        BigDecimal coefficientOfVariation = standardDeviation.divide(predictedAmount, 4, RoundingMode.HALF_UP);

        //convert to confidence score (0-100)
        BigDecimal confidence = BigDecimal.valueOf(100).subtract(coefficientOfVariation.multiply(BigDecimal.valueOf(100)));
        return confidence.max(BigDecimal.valueOf(20)).min(BigDecimal.valueOf(95));
    }

    //update accuracy after spending is known
    public void updateForecastAccuracy(Long userId, Integer month, Integer year) {
        List<Forecast> forecasts = forecastRepository.findEvaluableForecastsByUserId(userId, month, year);

        for(Forecast forecast : forecasts) {
            BigDecimal actualSpending = analyticsService.getCategorySpendingForMonth(
                    userId, forecast.getCategory().getId(), month, year);

            forecast.setActualAmount(actualSpending);
            forecast.setUpdatedDate(LocalDateTime.now());
            forecastRepository.save(forecast);
        }
    }

    //get forecast for accuracy
    public ForecastAccuracyReport getForecastAccuracyReport(Long userId) {
        List<Forecast> evaluableForecasts = forecastRepository.findEvaluableForecastsByUserId(
                userId,LocalDate.now().getYear(), LocalDate.now().getMonthValue());

        int totalForecasts = evaluableForecasts.size();
        int accurateForecasts = 0;
        BigDecimal totalAccuracy = BigDecimal.ZERO;

        for(Forecast forecast : evaluableForecasts) {
            if(forecast.getActualAmount() != null) {
                if(forecast.isAccurate()) {
                    accurateForecasts++;
                }
                BigDecimal accuracy = forecast.getAccuracyPercentage();
                if(accuracy != null) {
                    totalAccuracy = totalAccuracy.add(accuracy);
                }
            }
        }
        BigDecimal overallAccuracy = totalForecasts > 0 ?
                totalAccuracy.divide(BigDecimal.valueOf(totalForecasts), 2, RoundingMode.HALF_UP) : BigDecimal.ZERO;
        return new ForecastAccuracyReport(totalForecasts, accurateForecasts, overallAccuracy, evaluableForecasts);
    }

    public static class ForecastAccuracyReport {
        private int totalForecasts;
        private int accurateForecasts;
        private BigDecimal overallAccuracy;
        private List<Forecast> evaluableForecasts;


        public ForecastAccuracyReport(int totalForecasts, int accurateForecasts,
                                      BigDecimal overallAccuracy, List<Forecast> evaluableForecasts) {
            this.totalForecasts = totalForecasts;
            this.accurateForecasts = accurateForecasts;
            this.overallAccuracy = overallAccuracy;
            this.evaluableForecasts = evaluableForecasts;
        }

        public int getTotalForecasts() {
            return totalForecasts;
        }

        public int getAccurateForecasts() {
            return accurateForecasts;
        }

        public BigDecimal getOverallAccuracy() {
            return overallAccuracy;
        }

        public List<Forecast> getEvaluableForecasts() {
            return evaluableForecasts;
        }

        public double getAccuracyRate() {
            return totalForecasts > 0 ? (double) accurateForecasts / totalForecasts * 100 : 0.0;
        }
    }

    //old forecasts cleanup
    public void cleanupOldForecasts(int monthsToKeep) {
        LocalDate cutoffDate = LocalDate.now().minusMonths(monthsToKeep);
        forecastRepository.deleteOldForecasts(cutoffDate.getYear(), cutoffDate.getMonthValue());
    }

}
