package com.parkrabbit.backend.dto;

import java.math.BigDecimal;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ParkingLotAvailabilityResponse {
    
    private Long id;
    private String name;
    private String address;
    private Double latitude;
    private Double longitude;
    private BigDecimal hourlyRate;
    private Integer totalSlots;

    private Long freeSlots;
    private Long reservedSlots;
    private Long occupiedSlots;
    
}
