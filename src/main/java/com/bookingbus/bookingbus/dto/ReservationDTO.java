package com.bookingbus.bookingbus.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReservationDTO {
    private Integer ticketNumber;
    private String seatNumber;
    private char origin;
    private char destination;
}
