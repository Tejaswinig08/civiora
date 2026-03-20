package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoticeRepo extends JpaRepository<Notice, Long> {
    List<Notice> findAllByOrderByDateDesc();
}
