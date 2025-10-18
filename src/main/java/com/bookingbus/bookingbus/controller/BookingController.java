package com.bookingbus.bookingbus.controller;

import com.bookingbus.bookingbus.service.BookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/booking")
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService){
        this.bookingService = bookingService;
    }
    @GetMapping(value = "/availability-and-price")
    public ResponseEntity<String> checkAvailabilityAndPrice() {
        return ResponseEntity.ok(this.bookingService.checkAvailabilityAndPrice());
    }

    @GetMapping(value = "/tickets")
    public ResponseEntity<String> getTickets() {
        return ResponseEntity.ok(this.bookingService.getTickets());
    }
}
