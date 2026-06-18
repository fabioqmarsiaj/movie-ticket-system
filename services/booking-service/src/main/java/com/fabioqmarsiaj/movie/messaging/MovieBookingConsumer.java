package com.fabioqmarsiaj.movie.messaging;

import com.fabioqmarsiaj.movie.events.BookingCreatedEvent;
import com.fabioqmarsiaj.movie.service.BookingService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.MOVIE_BOOKING_EVENTS_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.MOVIE_BOOKING_GROUP;

@Component
@Slf4j
@AllArgsConstructor
public class MovieBookingConsumer {

    private BookingService service;

    @KafkaListener(topics = MOVIE_BOOKING_EVENTS_TOPIC, groupId = MOVIE_BOOKING_GROUP)
    public void processBookingRequest(final BookingCreatedEvent event) {
        try {
            log.info("BookingListener:: Consuming booking event for id : {}", event.bookingId());
            service.processBooking(event);
        } catch (Exception e) {
            log.error("BookingListener:: Error while processing booking event for id : {}", event.bookingId(), e);
        }
    }
}
