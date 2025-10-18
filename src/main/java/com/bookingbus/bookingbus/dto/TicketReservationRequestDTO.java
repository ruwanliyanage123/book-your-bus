package com.bookingbus.bookingbus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TicketReservationRequestDTO {
    private Integer passengerCount;
    private Character origin;
    private Character destination;
    private boolean priceConfirmation;
}
