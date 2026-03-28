package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepo extends JpaRepository<Complaint, Integer> {
    List<Complaint> findByUserIdOrderByCreatedAtDesc(int userId);
    List<Complaint> findAllByOrderByCreatedAtDesc();
}
