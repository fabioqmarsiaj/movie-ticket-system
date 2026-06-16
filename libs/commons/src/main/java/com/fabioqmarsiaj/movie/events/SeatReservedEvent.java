package com.fabioqmarsiaj.movie.events;

public record SeatReservedEvent(String bookingId, boolean reserved, long amount) {}