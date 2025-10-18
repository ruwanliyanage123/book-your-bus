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
public class AvailabilityAndPriceResponseDTO {
    private Integer availableSeatCount;
    private List<String> availableSeats;
    private Double totalPrice;
}
