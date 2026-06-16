package com.fabioqmarsiaj.movie.events;

public record BookingPaymentEvent(String bookingId, boolean paymentCompleted, long amount) {
}

