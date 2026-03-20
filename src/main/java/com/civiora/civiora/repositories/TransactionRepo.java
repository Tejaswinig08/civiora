package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Integer> {
    List<Transaction> findByUserIdOrderByDateDesc(int userId);

    // Total maintenance deposits (descriptions NOT starting with "Booking:")
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.description NOT LIKE 'Booking:%'")
    double sumMaintenanceIncome();

    // Total booking revenue (descriptions starting with "Booking:")
    @Query("SELECT COALESCE(SUM(t.amount), 0) FROM Transaction t WHERE t.description LIKE 'Booking:%'")
    double sumBookingIncome();

    // Count of maintenance deposits
    @Query("SELECT COUNT(t) FROM Transaction t WHERE t.description NOT LIKE 'Booking:%'")
    long countMaintenancePayments();
}
