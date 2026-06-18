package com.fabioqmarsiaj.movie.service;

import com.fabioqmarsiaj.movie.entity.SeatInventory;
import com.fabioqmarsiaj.movie.enums.SeatStatus;
import com.fabioqmarsiaj.movie.events.SeatReservedEvent;
import com.fabioqmarsiaj.movie.messaging.SeatReserveProducer;
import com.fabioqmarsiaj.movie.repository.SeatInventoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SeatInventoryService {

    private final SeatInventoryRepository seatInventoryRepository;
    private final SeatReserveProducer seatReserveProducer;

    public SeatInventoryService(SeatInventoryRepository seatInventoryRepository,
                                SeatReserveProducer seatReserveProducer) {
        this.seatReserveProducer = seatReserveProducer;
        this.seatInventoryRepository = seatInventoryRepository;
    }

    public void reserveSeats(SeatReservedEvent event) {

        log.info("SeatInventoryService:: Processing bookingCreated for bookingId {}", event.bookingId());

        List<SeatInventory> seats = seatInventoryRepository
                .findByShowIdAndSeatNumberIn(event.showId(), event.seatIds());

        boolean allAvailable = seats.stream()
                .allMatch(s -> s.getStatus() == SeatStatus.AVAILABLE);

        if (allAvailable) {
            seats.forEach(s -> {
                s.setStatus(SeatStatus.LOCKED);
                s.setCurrentBookingId(event.bookingId());
            });
            seatInventoryRepository.saveAll(seats);

            seatReserveProducer
                    .publishSeatReserveEvents(new SeatReservedEvent(event.bookingId(), event.showId(), event.seatIds(), true, event.amount()));
            log.info("SeatInventoryService:: Seats locked successfully for bookingId {}", event.bookingId());
        } else {
            log.warn("SeatInventoryService:: Seat locking failed for bookingId {}. Some seats are not available.", event.bookingId());

            seatReserveProducer
                    .publishSeatReserveEvents(new SeatReservedEvent(event.bookingId(), event.showId(), event.seatIds(), false, event.amount()));
        }
    }

    public void rollbackSeatReservationOnFailure(String bookingId) {
        log.info("SeatInventoryService:: Releasing seats for bookingId {}", bookingId);

        List<SeatInventory> bookingSeats = seatInventoryRepository.findByCurrentBookingId(bookingId);

        bookingSeats.forEach(s -> {
            s.setStatus(SeatStatus.AVAILABLE);
            s.setCurrentBookingId(null);
        });

        seatInventoryRepository.saveAll(bookingSeats);
        log.info("SeatInventoryService:: Seats released successfully for bookingId {}", bookingId);

        seatReserveProducer
                .publishSeatReserveEvents(new SeatReservedEvent(bookingId, null, null, false, 0));
    }
}
