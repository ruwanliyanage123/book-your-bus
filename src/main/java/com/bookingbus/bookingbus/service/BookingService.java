package com.bookingbus.bookingbus.service;

import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    String checkAvailabilityAndPrice();
    String getTickets();
}
