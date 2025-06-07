package com.financetracker.expense_tracker.repository;

import com.financetracker.expense_tracker.entity.Forecast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ForecastRepository extends JpaRepository<Forecast, Long> {

    List<Forecast> findByUserId(Long userId);

    List<Forecast> findByUserIdAndForecastMonthAndForecastYear(
            Long userId, Integer forecastMonth, Integer forecastYear);

    Optional<Forecast> findByUserIdAndCategoryIdAndForecastMonthAndForecastYear(
            Long userId, Long categoryId, Integer forecastMonth, Integer forecastYear);


    @Query("select f from Forecast f where f.user.id = :userId " +
            "order by f.forecastYear desc, f.forecastMonth desc, f.createdDate desc ")
    List<Forecast> findLatestForecastsByUserId(@Param("userId") Long userId);

    @Query("select f from Forecast f where f.user.id = :userId " +
            "and (f.forecastYear < :currentYear or "+
            "(f.forecastYear = :currentYear and f.forecastMonth< :currentMonth))")
    List<Forecast> findEvaluableForecastsByUserId(@Param("userId") Long userId,
                                                  @Param("currentYear") Integer currentYear,
                                                  @Param("currentMonth") Integer currentMonth);

    List<Forecast> findByUserIdAndAlgorithmUsed(Long userId, String algorithmUsed);

    @Query("delete from Forecast f where f.forecastYear < :cutoffYear or " +
            "(f.forecastYear = :cutoffYear and f.forecastMonth < :cutofMonth)")
    void deleteOldForecasts(@Param("cutoffYear") Integer cutoffYear,
                            @Param("cutoffMonth") Integer cutoffMonth);
}
