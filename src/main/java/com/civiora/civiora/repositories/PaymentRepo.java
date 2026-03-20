package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, Long> {
    List<Payment> findByStatusNot(String status);
    List<Payment> findByWingAndFlat(String wing, String flat);
}
