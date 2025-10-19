package com.bookingbus.bookingbus.service.impl;

import com.bookingbus.bookingbus.dto.AvailabilityAndPriceResponseDTO;
import com.bookingbus.bookingbus.dto.ReservationDTO;
import com.bookingbus.bookingbus.dto.TicketReservationRequestDTO;
import com.bookingbus.bookingbus.dto.TicketReservationResponseDTO;
import com.bookingbus.bookingbus.service.BookingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private static final char A = 'A';
    private static final char B = 'B';
    private static final char C = 'C';
    private static final char D = 'D';
    private final ReservationDTO[][] upJourney = new ReservationDTO[4][10];
    private final ReservationDTO[][] downJourney = new ReservationDTO[4][10];
    private int ticketNumberCounter = 0;

    public BookingServiceImpl() {
        initiateSeats();
        log.info("Empty Seats allocated for both journeys");
    }

    private void initiateSeats(){
        for (int i = 0; i < upJourney.length; i++) {
            char seatRow;
            if (i == 0) {
                seatRow = A;
            } else if (i == 1) {
                seatRow = B;
            } else if (i == 2) {
                seatRow = C;
            } else {
                seatRow = D;
            }
            for (int j = 0; j < upJourney[i].length; j++) {
                int seat = j + 1;
                String seatNumber = seatRow + String.valueOf(seat);
                upJourney[i][j] = new ReservationDTO(0, seatNumber, ' ', ' ');
                downJourney[i][j] = new ReservationDTO(0, seatNumber, ' ', ' ');
            }
        }
    }

    @Override
    public AvailabilityAndPriceResponseDTO checkAvailabilityAndPrice(Integer numberOfPassengers, Character origin, Character destination) {
        log.info("Checking availability and price for {} passengers from {} to {}", numberOfPassengers, origin, destination);
        if (numberOfPassengers <= 0) {
            throw new IllegalArgumentException("Number of passengers must be greater than zero");
        }
        if (!isValidTown(origin) || !isValidTown(destination)) {
            throw new IllegalArgumentException("Invalid origin or destination");
        }
        final List<String> availableSeats = getAvailableSeats(origin, destination).stream().map(ReservationDTO::getSeatNumber).toList();
        if (availableSeats.size() < numberOfPassengers) {
            String message = availableSeats.size() == 0 ? "Sorry! No seats available" : "Sorry! Only " + availableSeats.size() + " seats available";
            throw new IllegalArgumentException(message);
        }
        double totalPrice = calculatePrice(origin, destination, numberOfPassengers);
        return new AvailabilityAndPriceResponseDTO(availableSeats.size(), availableSeats, totalPrice);
    }

    @Override
    public TicketReservationResponseDTO reserveTickets(TicketReservationRequestDTO reservationRequestDTO) {
        if (reservationRequestDTO == null || reservationRequestDTO.getPassengerCount() <= 0) {
            throw new IllegalArgumentException("Invalid reservation request");
        }
        final List<Integer> ticketNumbers = new ArrayList<>();
        final List<String> seatNumbers = new ArrayList<>();
        char origin = reservationRequestDTO.getOrigin();
        char destination = reservationRequestDTO.getDestination();
        int passengerCount = reservationRequestDTO.getPassengerCount();
        if (!isValidTown(origin) || !isValidTown(destination)) {
            throw new IllegalArgumentException("Invalid origin or destination");
        }
        final List<ReservationDTO> availableSeats = getAvailableSeats(reservationRequestDTO.getOrigin(), reservationRequestDTO.getDestination());
        if (availableSeats.size() < reservationRequestDTO.getPassengerCount()) {
            throw new IllegalArgumentException("Not enough available seats");
        }
        for (int i = 0; i < passengerCount; i++) {
            ReservationDTO seat = availableSeats.get(i);
            ticketNumberCounter += 1;
            seat.setTicketNumber(ticketNumberCounter);
            seat.setOrigin(origin);
            seat.setDestination(destination);
            ticketNumbers.add(ticketNumberCounter);
            seatNumbers.add(seat.getSeatNumber());
        }
        final double totalPrice = calculatePrice(origin, destination, passengerCount);
        log.info("Reserved {} tickets from {} to {}. Ticket Numbers: {}, Seat Numbers: {}, Total Price: {}",
                passengerCount, origin, destination, ticketNumbers, seatNumbers, totalPrice);
        return new TicketReservationResponseDTO(ticketNumbers, seatNumbers, origin, destination, totalPrice);
    }

    public List<ReservationDTO> getAvailableSeats(char origin, char destination) {
        if (origin < destination) {
            log.info("Getting available seats from {} to {} and up journey selected", origin, destination);
            return getAvailableSeats(origin, destination, upJourney);
        } else if (origin > destination) {
            log.info("Getting available seats from {} to {} and down journey selected", origin, destination);
            return getAvailableSeats(origin, destination, downJourney);
        } else {
            throw new IllegalArgumentException("Invalid origin or destination selection");
        }
    }

    private List<ReservationDTO> getAvailableSeats(char origin, char destination, ReservationDTO[][] journey) {
        log.info("Getting available seats for journey from {} to {}", origin, destination);
        final List<ReservationDTO> availableSeats = new ArrayList<>();
        for (ReservationDTO[] seat : journey) {
            for (final ReservationDTO reservationDTO : seat) {
                if (reservationDTO.getTicketNumber() > 0) {//already allocated seats
                    if (origin < destination) {//for up journey
                        if (reservationDTO.getDestination() <= origin) {
                            availableSeats.add(reservationDTO);
                        }
                    } else {//for down journey
                        if (origin <= reservationDTO.getDestination()) {
                            availableSeats.add(reservationDTO);
                        }
                    }
                } else {//available seats
                    availableSeats.add(reservationDTO);
                }
            }
        }
        return availableSeats;
    }

    private Double calculatePrice(char origin, char destination, int numberOfPassengers) {
        double unitPrice;
        if ((origin == A && destination == B) || (origin == B && destination == A)) {
            unitPrice = 50.0;
        } else if ((origin == A && destination == C) || (origin == C && destination == A)) {
            unitPrice = 100.0;
        } else if ((origin == A && destination == D) || (origin == D && destination == A)) {
            unitPrice = 150.0;
        } else if ((origin == B && destination == C) || (origin == C && destination == B)) {
            unitPrice = 50.0;
        } else if ((origin == B && destination == D) || (origin == D && destination == B)) {
            unitPrice = 100.0;
        } else if ((origin == C && destination == D) || (origin == D && destination == C)) {
            unitPrice = 50.0;
        } else {
            throw new IllegalArgumentException("Invalid route");
        }
        double total = unitPrice * numberOfPassengers;
        log.info("Calculated total price: {} for journey from {} to {}", total, origin, destination);
        return total;
    }

    private boolean isValidTown(char town) {
        return town == A || town == B || town == C || town == D;
    }

}
