package com.parkrabbit.backend.dto;

import java.time.LocalDateTime;

public class SlotAutoAssignedEvent {

    private Long userId;
    private Long reservationId;
    private Long parkingLotId;
    private Long slotId;
    private LocalDateTime reservedAt;
    private String parkingLotName;
    private String parkingLotAddress;

    public SlotAutoAssignedEvent() {}

    public SlotAutoAssignedEvent(
            Long userId,
            Long reservationId,
            Long parkingLotId,
            Long slotId,
            LocalDateTime reservedAt,
            String parkingLotName,
            String parkingLotAddress
    ) {
        this.userId = userId;
        this.reservationId = reservationId;
        this.parkingLotId = parkingLotId;
        this.slotId = slotId;
        this.reservedAt = reservedAt;
        this.parkingLotName = parkingLotName;
        this.parkingLotAddress = parkingLotAddress;
    }

    public Long getUserId() { return userId; }
    public Long getReservationId() { return reservationId; }
    public Long getParkingLotId() { return parkingLotId; }
    public Long getSlotId() { return slotId; }
    public LocalDateTime getReservedAt() { return reservedAt; }
    public String getParkingLotName() { return parkingLotName; }
    public String getParkingLotAddress() { return parkingLotAddress; }
}
