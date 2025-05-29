package com.financetracker.expense_tracker;

import com.financetracker.expense_tracker.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan("com.financetracker.expense_tracker.entity")
public class ExpenseTrackerApplication implements CommandLineRunner {

	@Autowired
	private CategoryService categoryService;

	public static void main(String[] args) {

		SpringApplication.run(ExpenseTrackerApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		categoryService.initializeDefaultCategories();
	}

}
