package com.financetracker.expense_tracker.controller;

import com.financetracker.expense_tracker.entity.User;
import com.financetracker.expense_tracker.service.ReportService;
import com.financetracker.expense_tracker.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/reports")
public class ReportController {
    @Autowired private ReportService reportService;
    @Autowired private UserService userService;

    @GetMapping
    public String reportsPage() {
        return "reports/reports";
    }

    @PostMapping("/expense")
    public ResponseEntity<byte[]> getExpenseReport(@RequestParam String startDate,
                                                   @RequestParam String endDate,
                                                   @RequestParam(required = false) String reportTitle,
                                                   Authentication authentication) {
        User user = userService.findByUsername(authentication.getName()).orElse(null);
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        byte[] reportData = reportService.generateExpenseReport(user.getId(), start, end, reportTitle);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=expense_report.txt")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.TEXT_PLAIN_VALUE)
                .body(reportData);
    }

}
