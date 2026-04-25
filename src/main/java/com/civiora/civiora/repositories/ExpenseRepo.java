package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExpenseRepo extends JpaRepository<Expense, Integer> {
    Optional<Expense> findByName(String name);
}
