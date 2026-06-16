package com.fabioqmarsiaj.movie.resource;

import com.fabioqmarsiaj.movie.requests.BookingRequest;
import com.fabioqmarsiaj.movie.responses.BookingResponse;
import com.fabioqmarsiaj.movie.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/booking-service")
public class BookingResource {

    private final BookingService bookingService;

    public BookingResource(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @PostMapping("/bookSeat")
    public ResponseEntity<?> bookSeat(@RequestBody BookingRequest request) {
        BookingResponse response = bookingService.bookSeats(request);
        return ResponseEntity.ok(response);
    }
}
