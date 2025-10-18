package com.bookingbus.bookingbus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservationResponseDTO {
    private List<Integer> ticketNumbers;
    private List<String> seatNumbers;
    private Character origin;
    private Character destination;
    private Double totalPrice;
}
