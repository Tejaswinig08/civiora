package com.civiora.civiora.controller;

import com.civiora.civiora.models.Booking;
import com.civiora.civiora.models.Transaction;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.BookingRepo;
import com.civiora.civiora.repositories.TransactionRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired
    BookingRepo bookingRepo;

    @Autowired
    UserRepo userRepo;

    @Autowired
    TransactionRepo transactionRepo;

    // Facility capacities
    private static final Map<String, Integer> CAPACITY = Map.of(
            "party_hall", 1,
            "games_club", 15,
            "pool", 20,
            "gym", 30,
            "sports_court", 1
    );

    // Facility prices
    private static final Map<String, Integer> PRICES = Map.of(
            "party_hall", 2000,
            "games_club", 100,
            "pool", 150,
            "gym", 100,
            "sports_court", 500
    );

    @GetMapping("/bookings/availability")
    public Map<String, Object> checkAvailability(
            @RequestParam String facility,
            @RequestParam String date,
            @RequestParam String time) {

        int booked = bookingRepo.countByFacilityNameAndBookingDateAndBookingTime(facility, date, time);
        int maxCapacity = CAPACITY.getOrDefault(facility, 0);
        int available = Math.max(0, maxCapacity - booked);

        Map<String, Object> result = new HashMap<>();
        result.put("facility", facility);
        result.put("date", date);
        result.put("time", time);
        result.put("booked", booked);
        result.put("capacity", maxCapacity);
        result.put("available", available);
        result.put("isFull", available <= 0);
        result.put("price", PRICES.getOrDefault(facility, 0));

        return result;
    }

    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        int userId = Integer.parseInt(data.get("userId").toString());
        String facility = data.get("facility").toString();
        String date = data.get("date").toString();
        String time = data.get("time").toString();

        int maxCapacity = CAPACITY.getOrDefault(facility, 0);
        int booked = bookingRepo.countByFacilityNameAndBookingDateAndBookingTime(facility, date, time);

        if (booked >= maxCapacity) {
            response.put("status", "error");
            response.put("message", "Booking Full for this slot");
            return response;
        }

        Optional<User> optionalUser = userRepo.findById(userId);
        if (!optionalUser.isPresent()) {
            response.put("status", "error");
            response.put("message", "User not found");
            return response;
        }

        User user = optionalUser.get();
        int price = PRICES.getOrDefault(facility, 0);

        // Save booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFacilityName(facility);
        booking.setBookingDate(date);
        booking.setBookingTime(time);
        booking.setAmount(price);
        bookingRepo.save(booking);

        // Save transaction
        String facilityLabel = facility.replace("_", " ").toUpperCase();
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(price);
        txn.setCurrBalance(user.getBalance());
        txn.setDescription("Booking: " + facilityLabel + " on " + date + " at " + time);
        transactionRepo.save(txn);

        response.put("status", "success");
        response.put("message", "Booking confirmed!");
        response.put("bookingId", booking.getId());
        response.put("newBalance", user.getBalance());

        return response;
    }

    @GetMapping("/bookings/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable int userId) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
