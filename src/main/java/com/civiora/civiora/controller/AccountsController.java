package com.civiora.civiora.controller;

import com.civiora.civiora.models.Expense;
import com.civiora.civiora.repositories.BookingRepo;
import com.civiora.civiora.repositories.ExpenseRepo;
import com.civiora.civiora.repositories.TransactionRepo;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AccountsController {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    ExpenseRepo expenseRepo;

    @PostConstruct
    public void initExpenses() {
        if (expenseRepo.count() == 0) {
            expenseRepo.save(new Expense(0, "Security & Watchman Salary", 144000.0));
            expenseRepo.save(new Expense(0, "Electricity (Common Areas)", 84000.0));
            expenseRepo.save(new Expense(0, "Water Charges", 36000.0));
            expenseRepo.save(new Expense(0, "Lift Maintenance", 24000.0));
            expenseRepo.save(new Expense(0, "Housekeeping & Gardening", 48000.0));
            expenseRepo.save(new Expense(0, "Repairs & Civil Works", 30000.0));
            expenseRepo.save(new Expense(0, "Administrative Expenses", 12000.0));
            expenseRepo.save(new Expense(0, "Insurance Premium", 15000.0));
        }
    }

    @GetMapping("/accounts/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        double maintenanceIncome = transactionRepo.sumMaintenanceIncome();
        double bookingIncome     = transactionRepo.sumBookingIncome();
        long   maintenanceCount  = transactionRepo.countMaintenancePayments();
        double totalIncome       = maintenanceIncome + bookingIncome;

        List<Expense> allExpenses = expenseRepo.findAll();
        Map<String, Double> expensesMap = new LinkedHashMap<>();
        double totalExpenses = 0;
        for (Expense e : allExpenses) {
            expensesMap.put(e.getName(), e.getAmount());
            totalExpenses += e.getAmount();
        }
        
        double netBalance    = totalIncome - totalExpenses;

        result.put("maintenanceIncome", maintenanceIncome);
        result.put("bookingIncome", bookingIncome);
        result.put("totalIncome", totalIncome);
        result.put("maintenanceCount", maintenanceCount);
        result.put("expenses", expensesMap);
        result.put("totalExpenses", totalExpenses);
        result.put("netBalance", netBalance);

        return result;
    }

    // --- Admin Endpoints for Expenses ---
    
    @GetMapping("/admin/expenses")
    public List<Expense> getAllExpenses() {
        return expenseRepo.findAll();
    }
    
    @PostMapping("/admin/expenses")
    public Expense addExpense(@RequestBody Expense expense) {
        return expenseRepo.save(expense);
    }
    
    @PutMapping("/admin/expenses/{id}")
    public Expense updateExpense(@PathVariable int id, @RequestBody Expense expenseDetails) {
        return expenseRepo.findById(id).map(e -> {
            e.setName(expenseDetails.getName());
            e.setAmount(expenseDetails.getAmount());
            return expenseRepo.save(e);
        }).orElseThrow(() -> new RuntimeException("Expense not found"));
    }
    
    @DeleteMapping("/admin/expenses/{id}")
    public void deleteExpense(@PathVariable int id) {
        expenseRepo.deleteById(id);
    }
}
