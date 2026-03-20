package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {
    int countByFacilityNameAndBookingDateAndBookingTime(String facilityName, String bookingDate, String bookingTime);
    List<Booking> findByUserIdOrderByCreatedAtDesc(int userId);
}
