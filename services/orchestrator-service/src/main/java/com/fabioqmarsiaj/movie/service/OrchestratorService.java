package com.fabioqmarsiaj.movie.service;

import com.fabioqmarsiaj.movie.events.BookingCreatedEvent;
import com.fabioqmarsiaj.movie.events.SeatReservedEvent;
import com.fabioqmarsiaj.movie.requests.BookingRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.SEAT_RESERVED_CMD_TOPIC;


@Service
@Slf4j
@AllArgsConstructor
public class OrchestratorService {

    private KafkaTemplate<String, Object> kafkaTemplate;

    private static SeatReservedEvent buildSeatReservedEvents(BookingRequest request, String bookingId) {
        return new SeatReservedEvent(
                bookingId,
                request.showId(),
                request.seatIds(),
                true,
                request.amount()
        );
    }

    private static BookingCreatedEvent buildBookingCreateEvents(BookingRequest request, String bookingId) {
        return new BookingCreatedEvent(
                bookingId,
                request.userId(),
                request.showId(),
                request.seatIds(),
                request.amount(),
                "PENDING"
        );
    }

    public String createBooking(BookingRequest request) {

        var bookingId = generateBookingId();
        BookingCreatedEvent bookingCreatedEvent = buildBookingCreateEvents(request, bookingId);

        kafkaTemplate.send(
            MOVIE_BOOKING_EVENTS_TOPIC,
            bookingId,
            bookingCreatedEvent
        );

        log.info("MovieBookingOrchestrator:: Published bookingCreated event for bookingId: {}", bookingId);

        SeatReservedEvent seatReservedRequest = buildSeatReservedEvents(request, bookingId);

        kafkaTemplate.send(
            SEAT_RESERVED_CMD_TOPIC,
            bookingId,
            seatReservedRequest
        );

        log.info("MovieBookingOrchestrator:: Published seatReserved request for bookingId: {}", bookingId);

        return bookingId;
    }

    private String generateBookingId() {
        return UUID.randomUUID().toString().split("-")[0];
    }

}
