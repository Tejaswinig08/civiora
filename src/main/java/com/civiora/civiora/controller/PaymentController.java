package com.civiora.civiora.controller;

import com.civiora.civiora.models.Transaction;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.TransactionRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class PaymentController {

    @Autowired
    UserRepo userRepo;

    @Autowired
    TransactionRepo transactionRepo;

    @PostMapping("/deposit")
    public String deposit(@RequestBody Map<String, Object> paymentData) {
        int userId = Integer.parseInt(paymentData.get("userId").toString());
        int amount = Integer.parseInt(paymentData.get("amount").toString());
        String description = paymentData.get("description") != null
                ? paymentData.get("description").toString()
                : "";

        if (amount <= 0) {
            return "Invalid amount";
        }

        Optional<User> optionalUser = userRepo.findById(userId);

        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setBalance(user.getBalance() + amount);
            userRepo.save(user);

            // Save transaction record
            Transaction txn = new Transaction();
            txn.setUserId(userId);
            txn.setAmount(amount);
            txn.setCurrBalance(user.getBalance());
            txn.setDescription(description.isEmpty() ? "Deposit of ₹" + amount : description);
            transactionRepo.save(txn);

            return "Payment Successful";
        } else {
            return "User not found";
        }
    }

    @GetMapping("/passbook/{userId}")
    public List<Transaction> getPassbook(@PathVariable int userId) {
        return transactionRepo.findByUserIdOrderByDateDesc(userId);
    }
}
