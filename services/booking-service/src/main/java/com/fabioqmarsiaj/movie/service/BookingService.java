package com.fabioqmarsiaj.movie.service;

import com.fabioqmarsiaj.movie.entity.Booking;
import com.fabioqmarsiaj.movie.events.BookingCreatedEvent;
import com.fabioqmarsiaj.movie.mapper.BookingRequestToEntityMapper;
import com.fabioqmarsiaj.movie.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public void processBooking(BookingCreatedEvent request) {
        log.info("BookingService:: Processing booking for user {} for show {}", request.userId(), request.showId());

        Booking booking = null;
        Booking existingBooking = this.bookingRepository
                .findByBookingCode(request.bookingId());

        if (existingBooking == null) {
            log.info("BookingService:: Creating new booking for id {}", request.bookingId());
            booking = BookingRequestToEntityMapper.mapEvents(request);
        } else {
            log.info("BookingService:: Updating existing booking for id {}", request.bookingId());
            booking = updateExistingBooking(existingBooking, request);
        }

        var saved = bookingRepository.save(booking);
        log.info("BookingService:: Booking saved: reservation id {} | status {}",
                saved.getBookingCode(), saved.getStatus());
    }

    private Booking updateExistingBooking(Booking existing, BookingCreatedEvent event) {
        existing.setStatus(event.status());
        return existing;
    }
}
