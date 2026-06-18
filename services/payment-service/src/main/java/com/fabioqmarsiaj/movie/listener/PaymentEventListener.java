package com.fabioqmarsiaj.movie.listener;

import com.fabioqmarsiaj.movie.events.BookingPaymentEvent;
import com.fabioqmarsiaj.movie.service.PaymentService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.PAYMENT_EVENTS_CMD_TOPIC;
import static com.fabioqmarsiaj.movie.commons.KafkaConfigProperties.PAYMENT_EVENT_GROUP;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentEventListener {

    private final PaymentService paymentService;

    @KafkaListener(topics = PAYMENT_EVENTS_CMD_TOPIC, groupId = PAYMENT_EVENT_GROUP)
    public void onPaymentEvents(BookingPaymentEvent event) {
        try {
            log.info("PaymentEventListener:: Processing payment events");
            paymentService.processPayment(event);
        } catch (Exception e) {
            log.error("Error processing payment event for bookingId {}: {}", event.bookingId(), e.getMessage());
        }
    }
}
