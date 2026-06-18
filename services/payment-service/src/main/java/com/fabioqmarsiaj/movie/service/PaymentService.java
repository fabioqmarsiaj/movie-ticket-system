package com.fabioqmarsiaj.movie.service;

import com.fabioqmarsiaj.movie.producer.PaymentEventsProducer;
import com.fabioqmarsiaj.movie.events.BookingPaymentEvent;
import com.fabioqmarsiaj.movie.exception.PaymentServiceException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class PaymentService {

    @Autowired
    private PaymentEventsProducer eventsProducer;

    public void processPayment(BookingPaymentEvent event) {
        log.info("Processing payment event for bookingId: {}", event.bookingId());
        try {
            log.info("Processing payment for bookingId: {}", event.bookingId());

            // Simulate payment failure scenario
            if (event.amount() > 2000) {
                log.info("Payment amount exceeds limit for bookingId: {}", event.bookingId());
                eventsProducer.publishPaymentFailureEvent(event);
            } else {
                eventsProducer.publishPaymentSuccessEvent(event);
                log.info("✅ Payment successful for bookingId: {}", event.bookingId());
            }

        } catch (Exception e) {
            log.error("❌ Payment failed for bookingId: {}. Reason: {}", event.bookingId(), e.getMessage());
            throw new PaymentServiceException("Payment processing failed for bookingId: " + event.bookingId());
        }
    }
}
