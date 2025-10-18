package com.bookingbus.bookingbus.service.impl;

import com.bookingbus.bookingbus.dto.AvailabilityAndPriceResponseDTO;
import com.bookingbus.bookingbus.dto.TicketReservationRequestDTO;
import com.bookingbus.bookingbus.dto.TicketReservationResponseDTO;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class BookingServiceImplTest {

    private BookingServiceImpl bookingService;
    private static final char A = 'A';
    private static final char B = 'B';
    private static final char C = 'C';
    private static final char D = 'D';

    @BeforeMethod
    public void setUp() {
        bookingService = new BookingServiceImpl();
    }

    @Test
    public void testCheckAvailabilityAndPrice_Valid() {
        AvailabilityAndPriceResponseDTO response = bookingService.checkAvailabilityAndPrice(2, A, B);
        assertNotNull(response, "Response should not be null");
        assertTrue(response.getAvailableSeats().size() > 0, "Should have available seats");
        assertEquals(response.getTotalPrice(), 100.0, "Total price should be correct (50 * 2)");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*greater than zero.*")
    public void testCheckAvailabilityAndPrice_InvalidPassengerCount() {
        bookingService.checkAvailabilityAndPrice(0, A, B);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*Invalid origin or destination.*")
    public void testCheckAvailabilityAndPrice_InvalidTowns() {
        bookingService.checkAvailabilityAndPrice(2, 'X', 'Z');
    }

    @Test
    public void testReserveTickets_Valid() {
        TicketReservationRequestDTO request = new TicketReservationRequestDTO();
        request.setOrigin(A);
        request.setDestination(B);
        request.setPassengerCount(3);
        TicketReservationResponseDTO response = bookingService.reserveTickets(request);
        assertNotNull(response);
        assertEquals(response.getTicketNumbers().size(), 3, "Should reserve 3 tickets");
        assertEquals(response.getSeatNumbers().size(), 3, "Should have 3 seat numbers");
        assertEquals(response.getOrigin(), Character.valueOf(A));
        assertEquals(response.getDestination(), Character.valueOf(B));
        assertEquals(response.getTotalPrice(), 150.0, "Total price should be 50 * 3");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Invalid reservation request.*")
    public void testReserveTickets_InvalidPassengerCount() {
        TicketReservationRequestDTO request = new TicketReservationRequestDTO();
        request.setOrigin(A);
        request.setDestination(B);
        request.setPassengerCount(0);
        bookingService.reserveTickets(request);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Invalid origin or destination.*")
    public void testReserveTickets_InvalidRoute() {
        TicketReservationRequestDTO request = new TicketReservationRequestDTO();
        request.setOrigin('X');
        request.setDestination(B);
        request.setPassengerCount(1);
        bookingService.reserveTickets(request);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = ".*Not enough available seats.*")
    public void testReserveTickets_Overbooking() {
        TicketReservationRequestDTO request = new TicketReservationRequestDTO();
        request.setOrigin(A);
        request.setDestination(B);
        request.setPassengerCount(999); // exceed total seat count (4x10)
        bookingService.reserveTickets(request);
    }

    @Test
    public void testGetAvailableSeats_UpJourney() {
        assertFalse(bookingService.getAvailableSeats(A, B).isEmpty());
    }

    @Test
    public void testGetAvailableSeats_DownJourney() {
        assertFalse(bookingService.getAvailableSeats(C, A).isEmpty());
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = ".*Invalid origin or destination selection.*")
    public void testGetAvailableSeats_InvalidSameTown() {
        bookingService.getAvailableSeats(A, A);
    }

    @Test
    public void testCalculatePriceRoutes() throws Exception {
        double price1 = invokeCalculatePrice(A, B, 2);
        double price2 = invokeCalculatePrice(A, D, 1);
        assertEquals(price1, 100.0);
        assertEquals(price2, 150.0);
    }

    private double invokeCalculatePrice(char origin, char destination, int count) throws Exception {
        var method = BookingServiceImpl.class.getDeclaredMethod("calculatePrice", char.class, char.class, int.class);
        method.setAccessible(true);
        return (double) method.invoke(bookingService, origin, destination, count);
    }
}