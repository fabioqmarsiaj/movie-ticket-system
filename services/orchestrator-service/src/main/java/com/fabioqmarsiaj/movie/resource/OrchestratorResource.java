package com.fabioqmarsiaj.movie.resource;

import com.fabioqmarsiaj.movie.requests.BookingRequest;
import com.fabioqmarsiaj.movie.service.OrchestratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orchestrator")
@RequiredArgsConstructor
public class OrchestratorResource {

    private final OrchestratorService service;

    @PostMapping("/bookings")
    public String startSaga(@RequestBody BookingRequest request) {
        return service.createBooking(request);
    }
}
