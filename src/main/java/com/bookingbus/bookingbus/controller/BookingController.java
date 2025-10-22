package com.bookingbus.bookingbus.controller;

import com.bookingbus.bookingbus.dto.AvailabilityAndPriceResponseDTO;
import com.bookingbus.bookingbus.dto.TicketReservationRequestDTO;
import com.bookingbus.bookingbus.dto.TicketReservationResponseDTO;
import com.bookingbus.bookingbus.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(value = "/booking")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping(value = "/availability-and-price")
    public ResponseEntity<?> checkAvailabilityAndPrice(
            @RequestParam Integer numberOfPassengers,
            @RequestParam Character origin,
            @RequestParam Character destination
    ) {
        try {
            log.info("Start:- Checking availability and price for {} passengers from {} to {}", numberOfPassengers, origin, destination);
            final AvailabilityAndPriceResponseDTO responseDTO = bookingService.checkAvailabilityAndPrice(numberOfPassengers, origin, destination);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("Error checking availability and price: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/tickets", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> reserveTickets(@RequestBody TicketReservationRequestDTO ticketReservationRequestDTO) {
        try {
            log.info("Start:- Reserving tickets for request: {}", ticketReservationRequestDTO);
            final TicketReservationResponseDTO responseDTO = bookingService.reserveTickets(ticketReservationRequestDTO);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (Exception exception) {
            log.error("Error reserving tickets: {}", exception.getMessage());
            return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
