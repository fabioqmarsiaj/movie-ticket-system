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
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.PAYMENT_EVENTS_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.SEAT_RESERVED_CMD_TOPIC;

@Component
@Slf4j
@AllArgsConstructor
public class PaymentListener {

    private KafkaTemplate<String, Object> kafkaTemplate;

    @KafkaListener(topics = PAYMENT_EVENTS_TOPIC, groupId = ORCHESTRATOR_CONSUMER_GROUP)
    public void onPaymentStatus(BookingPaymentEvent event) {
        log.info("MovieBookingOrchestrator:: Consuming payment event for bookingId: {}", event.bookingId());
        if (event.paymentCompleted()) {
            confirmBookingStatus(event);
            log.info("Orchestrator published BookingCreatedEvent (CONFIRMED)");

        } else {
            SeatReservedEvent seatFailureEvent = new SeatReservedEvent(event.bookingId(), event.showId(), event.seatIds(), false, event.amount());
            kafkaTemplate.send(SEAT_RESERVED_CMD_TOPIC, event.bookingId(), seatFailureEvent);
            log.info("Orchestrator published SeatReservedEvent (release/failed) to {}", "Seat-Service");
        }
    }

    private void confirmBookingStatus(BookingPaymentEvent event) {
        BookingCreatedEvent bookingSuccessEvent = new BookingCreatedEvent(
                event.bookingId(),
                null,
                event.showId(),
                event.seatIds(),
                event.amount(),
                "CONFIRMED"
        );
        kafkaTemplate.send(MOVIE_BOOKING_EVENTS_TOPIC, event.bookingId(), bookingSuccessEvent);
    }
}
