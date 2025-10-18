package com.bookingbus.bookingbus.service;

import com.bookingbus.bookingbus.dto.AvailabilityAndPriceResponseDTO;
import com.bookingbus.bookingbus.dto.TicketReservationRequestDTO;
import com.bookingbus.bookingbus.dto.TicketReservationResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface BookingService {
    /**
     * Check availability and price for given number of passengers, origin and destination
     *
     * @param numberOfPassengers number of passengers
     * @param origin origin
     * @param destination destination
     * @return AvailabilityAndPriceResponseDTO containing available seats and total price
     */
    AvailabilityAndPriceResponseDTO checkAvailabilityAndPrice(Integer numberOfPassengers, Character origin, Character destination);

    /**
     * Reserve tickets based on the reservation request
     *
     * @param reservationRequestDTO reservation request details
     * @return TicketReservationResponseDTO containing reserved ticket details
     */
    TicketReservationResponseDTO reserveTickets(TicketReservationRequestDTO reservationRequestDTO);
}
