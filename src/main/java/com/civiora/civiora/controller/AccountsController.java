package com.civiora.civiora.controller;

import com.civiora.civiora.repositories.BookingRepo;
import com.civiora.civiora.repositories.TransactionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AccountsController {

    @Autowired
    TransactionRepo transactionRepo;

    @Autowired
    BookingRepo bookingRepo;

    // Hardcoded annual expenses (realistic society figures)
    private static final Map<String, Double> EXPENSES = new LinkedHashMap<>() {{
        put("Security & Watchman Salary", 144000.0);   // ₹12,000/month × 12
        put("Electricity (Common Areas)", 84000.0);    // ₹7,000/month × 12
        put("Water Charges", 36000.0);                 // ₹3,000/month × 12
        put("Lift Maintenance", 24000.0);              // ₹2,000/month × 12
        put("Housekeeping & Gardening", 48000.0);      // ₹4,000/month × 12
        put("Repairs & Civil Works", 30000.0);         // Yearly estimate
        put("Administrative Expenses", 12000.0);       // Stationery, printing, etc.
        put("Insurance Premium", 15000.0);             // Society insurance
    }};

    @GetMapping("/accounts/summary")
    public Map<String, Object> getSummary() {
        Map<String, Object> result = new LinkedHashMap<>();

        double maintenanceIncome = transactionRepo.sumMaintenanceIncome();
        double bookingIncome     = transactionRepo.sumBookingIncome();
        long   maintenanceCount  = transactionRepo.countMaintenancePayments();
        double totalIncome       = maintenanceIncome + bookingIncome;

        double totalExpenses = EXPENSES.values().stream().mapToDouble(Double::doubleValue).sum();
        double netBalance    = totalIncome - totalExpenses;

        result.put("maintenanceIncome", maintenanceIncome);
        result.put("bookingIncome", bookingIncome);
        result.put("totalIncome", totalIncome);
        result.put("maintenanceCount", maintenanceCount);
        result.put("expenses", EXPENSES);
        result.put("totalExpenses", totalExpenses);
        result.put("netBalance", netBalance);

        return result;
    }
}
