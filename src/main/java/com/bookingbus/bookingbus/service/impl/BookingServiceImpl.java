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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {
    private static final char A = 'A';
    private static final char B = 'B';
    private static final char C = 'C';
    private static final char D = 'D';
    private final ReservationDTO[][] upJourney = new ReservationDTO[4][10];
    private final ReservationDTO[][] downJourney = new ReservationDTO[4][10];
    private final AtomicBoolean isWriting = new AtomicBoolean(false);
    private final AtomicBoolean isReading = new AtomicBoolean(false);
    private final AtomicInteger ticketNumberCounter = new AtomicInteger(0);
    private final Object lock = new Object();

    public BookingServiceImpl() {
        initiateSeats();
        log.debug("Empty Seats allocated for both journeys");
    }

    private void initiateSeats() {
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
        log.info("Available seats for reservation: {}", availableSeats.size());
        if (availableSeats.size() < reservationRequestDTO.getPassengerCount()) {
            throw new IllegalArgumentException("Not enough available seats");
        }
        waitExecution(isReading, "Write");
        isWriting.set(true);
        for (int i = 0; i < passengerCount; i++) {
            ReservationDTO seat = availableSeats.get(i);
            int ticketNumber = ticketNumberCounter.addAndGet(1);
            seat.setTicketNumber(ticketNumber);
            seat.setOrigin(origin);
            seat.setDestination(destination);
            ticketNumbers.add(ticketNumber);
            seatNumbers.add(seat.getSeatNumber());
        }
        isWriting.set(false);
        notifyExecution("Write");
        final double totalPrice = calculatePrice(origin, destination, passengerCount);
        log.debug("Reserved {} tickets from {} to {}. Ticket Numbers: {}, Seat Numbers: {}, Total Price: {}",
                passengerCount, origin, destination, ticketNumbers, seatNumbers, totalPrice);
        return new TicketReservationResponseDTO(ticketNumbers, seatNumbers, origin, destination, totalPrice);
    }

    public List<ReservationDTO> getAvailableSeats(char origin, char destination) {
        if (origin < destination) {
            log.debug("Getting available seats from {} to {} and up journey selected", origin, destination);
            return getAvailableSeats(origin, destination, upJourney);
        } else if (origin > destination) {
            log.debug("Getting available seats from {} to {} and down journey selected", origin, destination);
            return getAvailableSeats(origin, destination, downJourney);
        } else {
            throw new IllegalArgumentException("Invalid origin or destination selection");
        }
    }

    private List<ReservationDTO> getAvailableSeats(char origin, char destination, ReservationDTO[][] journey) {
        log.debug("Getting available seats for journey from {} to {}", origin, destination);
        waitExecution(isWriting, "Read");
        isReading.set(true);
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
        isReading.set(false);
        notifyExecution("Read");
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
        log.debug("Calculated total price: {} for journey from {} to {}", total, origin, destination);
        return total;
    }

    private void waitExecution(AtomicBoolean flag, String operation) {
        synchronized (lock) {
            if (flag.get()) {
                try {
                    log.debug(operation + " operation waiting for other operation to complete");
                    lock.wait();
                } catch (InterruptedException e) {
                    log.error("Thread interrupted while waiting for write operation to complete", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public void notifyExecution(String operation) {
        synchronized (lock) {
            log.debug("Notify all the operation waiting for other operation to complete");
            lock.notifyAll();
        }
    }

    private boolean isValidTown(char town) {
        return town == A || town == B || town == C || town == D;
    }

}
