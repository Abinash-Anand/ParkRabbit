package com.parkrabbit.backend.dto;

import java.time.LocalDateTime;

public class ReservationExpiredEvent {

    private Long reservationId;
    private Long userId;
    private Long parkingLotId;
    private Long slotId;
    private LocalDateTime expiredAt;

    public ReservationExpiredEvent(
            Long reservationId,
            Long userId,
            Long parkingLotId,
            Long slotId,
            LocalDateTime expiredAt
    ) {
        this.reservationId = reservationId;
        this.userId = userId;
        this.parkingLotId = parkingLotId;
        this.slotId = slotId;
        this.expiredAt = expiredAt;
    }

    public Long getReservationId() {
        return reservationId;
    }

    public Long getUserId() {
        return userId;
    }

    public Long getParkingLotId() {
        return parkingLotId;
    }

    public Long getSlotId() {
        return slotId;
    }

    public LocalDateTime getExpiredAt() {
        return expiredAt;
    }
}
