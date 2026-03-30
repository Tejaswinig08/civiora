package com.civiora.civiora.repositories;

import com.civiora.civiora.models.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer> {

    int countByFacilityNameAndBookingDateAndBookingTime(String facilityName, String bookingDate, String bookingTime);

    List<Booking> findByUserIdOrderByCreatedAtDesc(int userId);

    /** Fetch all confirmed bookings for a facility on a date — used for Java-side overlap detection. */
    List<Booking> findByFacilityNameAndBookingDateAndStatus(String facilityName, String bookingDate, String status);

    /**
     * Generic time-range overlap detection.
     * Returns the count of CONFIRMED bookings for the given facility + date
     * whose time range overlaps with [startTime, endTime).
     *
     * Overlap condition: existingStart < requestedEnd  AND  existingEnd > requestedStart
     * (Works for any resource — not hardcoded to specific facilities.)
     */
    @Query("""
        SELECT COUNT(b) FROM Booking b
        WHERE b.facilityName = :facilityName
          AND b.bookingDate  = :date
          AND b.status       = 'confirmed'
          AND b.startTime IS NOT NULL
          AND b.endTime   IS NOT NULL
          AND b.startTime < :endTime
          AND b.endTime   > :startTime
        """)
    int countConflicting(
        @Param("facilityName") String facilityName,
        @Param("date")         String date,
        @Param("startTime")    String startTime,
        @Param("endTime")      String endTime
    );
}
