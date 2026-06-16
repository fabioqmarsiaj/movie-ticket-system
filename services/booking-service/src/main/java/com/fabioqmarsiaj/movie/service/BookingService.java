package com.fabioqmarsiaj.movie.service;

import com.fabioqmarsiaj.movie.entity.Booking;
import com.fabioqmarsiaj.movie.events.BookingCreatedEvent;
import com.fabioqmarsiaj.movie.mapper.BookingRequestToEntityMapper;
import com.fabioqmarsiaj.movie.mapper.EntityToBookingResponseMapper;
import com.fabioqmarsiaj.movie.messaging.BookingEventProducer;
import com.fabioqmarsiaj.movie.repository.BookingRepository;
import com.fabioqmarsiaj.movie.requests.BookingRequest;
import com.fabioqmarsiaj.movie.responses.BookingResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class BookingService {
    private final BookingRepository bookingRepository;
    private final BookingEventProducer bookingEventProducer;

    public BookingService(BookingRepository bookingRepository, BookingEventProducer bookingEventProducer) {
        this.bookingEventProducer = bookingEventProducer;
        this.bookingRepository = bookingRepository;
    }

    public BookingResponse bookSeats(BookingRequest request) {

        log.info("Booking seats for user {} for show {}", request.userId(), request.showId());

        // Map request -> entity
        var reservationEntity = BookingRequestToEntityMapper.map(request);

        // Persist and map to response
        var savedReservation = bookingRepository.save(reservationEntity);

        // Publish booking created event
        var bookingCreatedEvent = buildBookingCreateEvents(savedReservation);
        bookingEventProducer.publishBookingEvents(bookingCreatedEvent);

        var response = EntityToBookingResponseMapper.map(savedReservation);

        log.info("Seats confirmed with reservation id {}", response.reservationId());
        return response;
    }

    private BookingCreatedEvent buildBookingCreateEvents(Booking savedReservation) {
        return new BookingCreatedEvent(savedReservation.getBookingCode(), savedReservation.getUserId(), savedReservation.getShowId(), savedReservation.getSeatIds(), savedReservation.getAmount());
    }


    public void handleBookingOnSeatReservationFailure(String bookingId) {
        log.info("BookingService:: Handling booking failure for bookingId {}", bookingId);
        var bookingDetails=bookingRepository.findByBookingCode(bookingId);
        if(bookingDetails!=null){
            bookingDetails.setStatus("FAILED");
            bookingRepository.save(bookingDetails);
            log.info("BookingService:: Booking marked as FAILED for bookingId {}", bookingId);
        }else{
            log.warn("BookingService:: No booking found with bookingId {}", bookingId);
        }

    }
}
