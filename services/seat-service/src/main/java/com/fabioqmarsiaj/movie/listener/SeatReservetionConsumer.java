package com.fabioqmarsiaj.movie.listener;

import com.fabioqmarsiaj.movie.events.SeatReservedEvent;
import com.fabioqmarsiaj.movie.service.SeatInventoryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.SEAT_EVENT_GROUP;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.SEAT_RESERVED_CMD_TOPIC;

@Component
@Slf4j
@AllArgsConstructor
public class SeatReservetionConsumer {

    private final SeatInventoryService service;

    @KafkaListener(topics = SEAT_RESERVED_CMD_TOPIC, groupId = SEAT_EVENT_GROUP)
    public void onSeatReserveEvent(SeatReservedEvent event) {
        log.info("SeatReserveEventListener:: Consuming seat reserve event");

        if (event.reserved()) {
            service.reserveSeats(event);
            log.info("SeatReserveEventListener:: Seats reserved successfully for bookingId {}", event.bookingId());
        } else {
            service.rollbackSeatReservationOnFailure(event.bookingId());
            log.warn("SeatReserveEventListener:: Seat reservation failed for bookingId {}. Rolling back any locked seats.", event.bookingId());
        }
    }
}
