package com.fabioqmarsiaj.movie.mapper;

import com.fabioqmarsiaj.movie.entity.Booking;
import com.fabioqmarsiaj.movie.responses.BookingResponse;

public class EntityToBookingResponseMapper {
    public static BookingResponse map(Booking booking) {
        return new BookingResponse(booking.getBookingCode(),
                booking.getStatus());
    }
}
