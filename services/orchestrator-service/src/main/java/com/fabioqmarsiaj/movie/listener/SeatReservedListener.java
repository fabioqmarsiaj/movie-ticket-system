package com.fabioqmarsiaj.movie.listener;

import com.fabioqmarsiaj.movie.events.BookingCreatedEvent;
import com.fabioqmarsiaj.movie.events.BookingPaymentEvent;
import com.fabioqmarsiaj.movie.events.SeatReservedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.ORCHESTRATOR_CONSUMER_GROUP;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.PAYMENT_EVENTS_CMD_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.SEAT_RESERVED_TOPIC;


@Component
@Slf4j
@AllArgsConstructor
public class SeatReservedListener {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = SEAT_RESERVED_TOPIC, groupId = ORCHESTRATOR_CONSUMER_GROUP)
    public void onSeatReserved(SeatReservedEvent event) {
        log.info("MovieBookingOrchestrator:: Consuming seatReserved event for bookingId: {}", event.bookingId());

        if (event.reserved()) {
            sendPaymentRequest(event);
            log.info("Orchestrator published BookingPaymentEvent (request) to {}", "Payment-Service");
        } else {
            sendRollbackToBookingService(event);
            log.info("Orchestrator published BookingCreatedEvent (FAILED) to {}", "Booking-Service");
        }
    }

    private void sendRollbackToBookingService(SeatReservedEvent event) {

        BookingCreatedEvent bookingFailureEvent = new BookingCreatedEvent(
                event.bookingId(),
                null,
                event.showId(),
                event.seatIds(),
                event.amount(),
                "FAILED"
        );
        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, event.bookingId(), bookingFailureEvent);
    }

    private void sendPaymentRequest(SeatReservedEvent event) {
        BookingPaymentEvent paymentEvent = new BookingPaymentEvent(event.bookingId(), event.showId(), event.seatIds(), false, event.amount());
        kafkaTemplate.send(PAYMENT_EVENTS_CMD_TOPIC, event.bookingId(), paymentEvent);
    }
}
