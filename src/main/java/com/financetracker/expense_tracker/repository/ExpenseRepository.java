package com.financetracker.expense_tracker.repository;

import com.financetracker.expense_tracker.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
@Repository
public interface ExpenseRepository extends JpaRepository<Expense, Long> {

    List<Expense> findByUserId(Long userId);
    List<Expense> findByUserIdAndCategoryId(Long userId, Long categoryId);
    List<Expense> findByUserIdOrderByExpenseDateDesc(Long userId);

    //monthly spend
    @Query("SELECT e.category.id as categoryId, e.category.name as categoryName," +
    "e.category.colorCode as categoryColor, e.category.icon as categoryIcon, " +
    "MONTH(e.expenseDate) as month, YEAR(e.expenseDate) as year, " +
    "SUM(e.amount) as totalAmount, COUNT(e) as expenseCount " +
    "FROM Expense e " +
    "WHERE e.user.id = :userId " +
    "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year " +
    "GROUP BY e.category.id, e.category.name, e.category.colorCode, e.category.icon, " +
    "MONTH(e.expenseDate), YEAR(e.expenseDate)")
    List<Object[]> findMonthlySpendingByCategory(@Param("userId") Long userId,
                                                 @Param("month") Integer month,
                                                 @Param("year") Integer year );


    @Query("SELECT SUM(e.amount) FROM Expense e " +
    "WHERE e.user.id = :userId " +
    "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal findTotalMonthlySpending(@Param("userId") Long userId,
                                        @Param("month") Integer month,
                                        @Param("year") Integer year );

    @Query("SELECT SUM(e.amount) FROM Expense e " +
    "WHERE e.user.id = :userId AND e.category.id = :categoryId " +
    "AND MONTH(e.expenseDate) = :month AND YEAR(e.expenseDate) = :year")
    BigDecimal findMonthlySpendingByUserAndCategory(@Param("userId") Long userId,
                                                    @Param("categoryId") Long categoryId,
                                                    @Param("month") Integer month,
                                                    @Param("year") Integer year);

    @Query("SELECT MONTH(e.expenseDate) as month, YEAR(e.expenseDate) as year, " +
    "SUM(e.amount) as totalAmount " +
    "FROM Expense e " +
    "WHERE e.user.id = :userId " +
    "AND e.expenseDate >= :startDate " +
    "GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) " +
    "ORDER BY YEAR(e.expenseDate) DESC, MONTH(e.expenseDate) DESC")
    List<Object[]> findSpendingTrends(@Param("userId") Long userId,
                                      @Param("startDate") LocalDate startDate);

    @Query("SELECT MONTH(e.expenseDate) as month, YEAR(e.expenseDate) as year, " +
            "SUM(e.amount) as totalAmount " +
            "FROM Expense e " +
            "WHERE e.user.id = :userId AND e.category.id = :categoryId " +
            "AND e.expenseDate >= :startDate " +
            "GROUP BY YEAR(e.expenseDate), MONTH(e.expenseDate) " +
            "ORDER BY YEAR(e.expenseDate) DESC, MONTH(e.expenseDate) DESC")
    List<Object[]> findCategorySpendingTrends(@Param("userId") Long userId,
                                              @Param("categoryId") Long categoryId,
                                              @Param("startDate") LocalDate startDate);


    @Query("SELECT e FROM Expense e " +
    "WHERE e.user.id = :userId " +
    "ORDER BY e.expenseDate DESC, e.createdDate DESC")
    List<Expense> findRecentExpensesByUserId(@Param("userId") Long userId);


    @Query("select e from Expense e WHERE e.user.id = :userId " +
            "and (lower(e.description) like lower(concat('%', :searchTerm, '%') ) " +
            " or lower(e.category.name) like lower(concat('%', :searchTerm, '%'))) " +
            "order by e.expenseDate DESC")
    List<Expense> searchExpenses(@Param("userId") Long userId,
                                 @Param("searchTerm") String searchTerm);

    @Query("select e from Expense e WHERE e.user.id = :userId " +
            "AND e.expenseDate BETWEEN :startDate AND :endDate " +
            "order by e.expenseDate desc")
    List<Expense> findExpensesByDateRange(@Param("userId") Long userId,
                                          @Param("startDate") LocalDate startDate,
                                          @Param("endDate") LocalDate endDate);

}
