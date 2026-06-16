package com.fabioqmarsiaj.movie.repository;

import com.fabioqmarsiaj.movie.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    Booking findByBookingCode(String bookingId);
}

