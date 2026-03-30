package com.civiora.civiora.controller;

import com.civiora.civiora.models.Booking;
import com.civiora.civiora.models.Transaction;
import com.civiora.civiora.models.User;
import com.civiora.civiora.repositories.BookingRepo;
import com.civiora.civiora.repositories.TransactionRepo;
import com.civiora.civiora.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin(origins = "*")
public class BookingController {

    @Autowired BookingRepo bookingRepo;
    @Autowired UserRepo userRepo;
    @Autowired TransactionRepo transactionRepo;

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

    // ============================================================
    // TIME HELPERS
    // ============================================================

    /**
     * Convert "06:00 AM" / "01:00 PM" → "06:00" / "13:00" (24h HH:mm).
     * Alphabetical comparison of zero-padded 24h strings equals numeric
     * comparison, so we can use String.compareTo() for overlap detection.
     */
    private static String to24h(String timeStr) {
        if (timeStr == null || timeStr.isBlank()) return "";
        timeStr = timeStr.trim();
        String[] parts = timeStr.split("\\s+");
        if (parts.length < 2) return timeStr; // already 24h or unknown format
        String[] hm = parts[0].split(":");
        int hours   = Integer.parseInt(hm[0]);
        int minutes = Integer.parseInt(hm[1]);
        String period = parts[1].toUpperCase();
        if (period.equals("PM") && hours != 12) hours += 12;
        if (period.equals("AM") && hours == 12) hours = 0;
        return String.format("%02d:%02d", hours, minutes);
    }

    /** Add 60 minutes to a 24h "HH:mm" string. */
    private static String addOneHour(String time24) {
        if (time24 == null || time24.isBlank()) return "";
        String[] parts = time24.split(":");
        int total = Integer.parseInt(parts[0]) * 60 + Integer.parseInt(parts[1]) + 60;
        return String.format("%02d:%02d", (total / 60) % 24, total % 60);
    }

    /**
     * Get the effective start time of an existing booking as a 24h string.
     *
     * Priority:
     *   1. If startTime column is set AND is before endTime → use it.
     *   2. Parse from bookingTime (group format "12:00 PM - 05:00 PM").
     *   3. Fall back to to24h(bookingTime) for single-time slots.
     */
    private static String effectiveStart(Booking b) {
        String st = b.getStartTime();
        String et = b.getEndTime();

        // If both are stored AND correctly ordered, use them directly
        if (isValidRange(st, et)) return st;

        // Parse from bookingTime string (covers legacy NULL rows)
        String bt = b.getBookingTime();
        if (bt != null && bt.contains(" - ")) {
            // Format: "12:00 PM - 05:00 PM"
            String raw = bt.split(" - ")[0].trim();
            return to24h(raw);
        }
        // Single-time slot (e.g. gym "09:00 AM")
        return to24h(bt);
    }

    /**
     * Get the effective end time of an existing booking as a 24h string.
     * Same fallback logic as effectiveStart.
     */
    private static String effectiveEnd(Booking b) {
        String st = b.getStartTime();
        String et = b.getEndTime();

        if (isValidRange(st, et)) return et;

        String bt = b.getBookingTime();
        if (bt != null && bt.contains(" - ")) {
            String raw = bt.split(" - ")[1].trim();
            return to24h(raw);
        }
        // Capacity-based single slot → 1-hour duration
        return addOneHour(to24h(bt));
    }

    /** Returns true if both values are non-blank and start < end. */
    private static boolean isValidRange(String start, String end) {
        return start != null && !start.isBlank()
            && end   != null && !end.isBlank()
            && start.compareTo(end) < 0;
    }

    /**
     * Core overlap formula (exclusive-end intervals):
     *   reqStart < existEnd  AND  reqEnd > existStart
     *
     * This is the only place conflict logic lives – works for ANY resource.
     */
    private static boolean overlaps(String reqStart, String reqEnd,
                                    String existStart, String existEnd) {
        if (reqStart.isBlank() || reqEnd.isBlank()
         || existStart.isBlank() || existEnd.isBlank()) return false;
        return reqStart.compareTo(existEnd)   < 0
            && reqEnd.compareTo(existStart)   > 0;
    }

    /**
     * Java-side conflict detection that works for ALL bookings regardless of
     * whether startTime/endTime columns are populated (handles legacy NULL rows
     * by parsing from the bookingTime string field instead).
     *
     * @param excludeId  Pass a booking ID to exclude (e.g. the booking being
     *                   updated). Pass -1 to include all.
     */
    private boolean hasTimeConflict(String facility, String date,
                                    String reqStart, String reqEnd,
                                    int excludeId) {
        if (reqStart.isBlank() || reqEnd.isBlank()) return false;

        List<Booking> existing =
            bookingRepo.findByFacilityNameAndBookingDateAndStatus(facility, date, "confirmed");

        for (Booking b : existing) {
            if (b.getId() == excludeId) continue;
            String exStart = effectiveStart(b);
            String exEnd   = effectiveEnd(b);
            if (overlaps(reqStart, reqEnd, exStart, exEnd)) {
                return true;
            }
        }
        return false;
    }

    // ============================================================
    // ENDPOINTS
    // ============================================================

    /** Existing capacity-based availability check (unchanged). */
    @GetMapping("/bookings/availability")
    public Map<String, Object> checkAvailability(
            @RequestParam String facility,
            @RequestParam String date,
            @RequestParam String time) {

        int booked = bookingRepo.countByFacilityNameAndBookingDateAndBookingTime(facility, date, time);
        int maxCapacity = CAPACITY.getOrDefault(facility, 0);
        int available = Math.max(0, maxCapacity - booked);

        Map<String, Object> result = new HashMap<>();
        result.put("facility",  facility);
        result.put("date",      date);
        result.put("time",      time);
        result.put("booked",    booked);
        result.put("capacity",  maxCapacity);
        result.put("available", available);
        result.put("isFull",    available <= 0);
        result.put("price",     PRICES.getOrDefault(facility, 0));
        return result;
    }

    /**
     * Generic time-range conflict check — now uses Java-side detection so it
     * correctly handles legacy bookings whose startTime/endTime are NULL.
     *
     * GET /bookings/conflict-check?facility=&date=&startTime=HH:mm&endTime=HH:mm
     * Returns: { conflict: true/false, message: "..." }
     */
    @GetMapping("/bookings/conflict-check")
    public Map<String, Object> conflictCheck(
            @RequestParam String facility,
            @RequestParam String date,
            @RequestParam String startTime,
            @RequestParam String endTime) {

        Map<String, Object> result = new HashMap<>();
        boolean conflict = hasTimeConflict(facility, date, startTime, endTime, -1);
        result.put("conflict", conflict);
        if (conflict) {
            result.put("message", "This time slot is already booked. Please select a different time.");
        }
        return result;
    }

    /**
     * Create a booking with BOTH:
     *   1. Capacity check (multi-user facilities like gym/pool).
     *   2. Time-range overlap check via Java-side detection — handles legacy
     *      NULL rows and stored data in any format.
     *
     * @Transactional prevents TOCTOU race conditions.
     */
    @Transactional
    @PostMapping("/bookings")
    public Map<String, Object> createBooking(@RequestBody Map<String, Object> data) {
        Map<String, Object> response = new HashMap<>();

        int    userId   = Integer.parseInt(data.get("userId").toString());
        String facility = data.get("facility").toString();
        String date     = data.get("date").toString();
        String time     = data.get("time").toString();

        // 24h range fields sent by the frontend
        String startTime = data.containsKey("startTime") ? data.get("startTime").toString() : "";
        String endTime   = data.containsKey("endTime")   ? data.get("endTime").toString()   : "";

        // Derive from time string if frontend didn't send them (safety net)
        if (startTime.isBlank() && !time.isBlank()) {
            if (time.contains(" - ")) {
                startTime = to24h(time.split(" - ")[0].trim());
                endTime   = to24h(time.split(" - ")[1].trim());
            } else {
                startTime = to24h(time);
                endTime   = addOneHour(startTime);
            }
        }

        // ── 1. Capacity check ──────────────────────────────────────────────
        int maxCapacity = CAPACITY.getOrDefault(facility, 0);
        int booked = bookingRepo.countByFacilityNameAndBookingDateAndBookingTime(facility, date, time);
        if (booked >= maxCapacity) {
            response.put("status",  "error");
            response.put("message", "Booking Full for this slot");
            return response;
        }

        // ── 2. Time-range overlap check (Java-side, handles NULL legacy rows) ──
        if (hasTimeConflict(facility, date, startTime, endTime, -1)) {
            response.put("status",  "conflict");
            response.put("message", "This time slot is already booked. Please select a different time.");
            return response;
        }

        // ── 3. Validate user ───────────────────────────────────────────────
        Optional<User> optionalUser = userRepo.findById(userId);
        if (!optionalUser.isPresent()) {
            response.put("status",  "error");
            response.put("message", "User not found");
            return response;
        }

        User user  = optionalUser.get();
        int  price = PRICES.getOrDefault(facility, 0);

        // ── 4. Save booking ────────────────────────────────────────────────
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setFacilityName(facility);
        booking.setBookingDate(date);
        booking.setBookingTime(time);       // kept for display / backward compat
        booking.setStartTime(startTime);    // 24h HH:mm
        booking.setEndTime(endTime);        // 24h HH:mm
        booking.setAmount(price);
        bookingRepo.save(booking);

        // ── 5. Save transaction ────────────────────────────────────────────
        String facilityLabel = facility.replace("_", " ").toUpperCase();
        Transaction txn = new Transaction();
        txn.setUserId(userId);
        txn.setAmount(price);
        txn.setCurrBalance(user.getBalance());
        txn.setDescription("Booking: " + facilityLabel + " on " + date + " at " + time);
        transactionRepo.save(txn);

        response.put("status",    "success");
        response.put("message",   "Booking confirmed!");
        response.put("bookingId", booking.getId());
        response.put("newBalance", user.getBalance());
        return response;
    }

    @GetMapping("/bookings/user/{userId}")
    public List<Booking> getUserBookings(@PathVariable int userId) {
        return bookingRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
