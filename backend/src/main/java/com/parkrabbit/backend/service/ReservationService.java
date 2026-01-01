package com.parkrabbit.backend.service;

import com.parkrabbit.backend.dto.ReservationResponseDto;
import com.parkrabbit.backend.entity.ParkingSlot;
import com.parkrabbit.backend.entity.Reservation;
import com.parkrabbit.backend.entity.ReservationStatus;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;
import com.parkrabbit.backend.repository.ParkingSlotRepository;
import com.parkrabbit.backend.repository.ReservationRepository;
import com.parkrabbit.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.parkrabbit.backend.entity.ReservationQueue;
import com.parkrabbit.backend.entity.User;
import com.parkrabbit.backend.repository.ReservationQueueRepository;


import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ParkingSlotRepository slotRepository;
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final ReservationQueueRepository queueRepository;


    public ReservationService(
            ParkingSlotRepository slotRepository,
            ReservationRepository reservationRepository,
            ReservationQueueRepository queueRepository,
            UserRepository userRepository
    ) {
        this.slotRepository = slotRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ReservationResponseDto reserveSlot(Long parkingLotId, Long userId) {

        // 1️⃣ Enforce one active reservation per user
        boolean hasActiveReservation =
                reservationRepository.existsByUserIdAndStatus(
                        userId,
                        ReservationStatus.ACTIVE
                );

        if (hasActiveReservation) {
            throw new IllegalStateException(
                    "User already has an active reservation"
            );
        }

        // 2️⃣ Try to find FREE slot
        ParkingSlot slot = slotRepository
                .findFirstByParkingLotIdAndStatus(
                        parkingLotId,
                        ParkingSlotStatus.FREE
                )
                .orElse(null);

        // 3️⃣ If NO slot → add user to queue
        if (slot == null) {

            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            if (!queueRepository.existsByUserAndParkingLotId(user, parkingLotId)) {
                queueRepository.save(new ReservationQueue(user, parkingLotId));
            }

            ReservationResponseDto response = new ReservationResponseDto();
            response.setQueued(true);
            response.setMessage("All slots full. User added to queue.");

            return response;
        }

        // 4️⃣ Slot found → reserve it
        slot.setStatus(ParkingSlotStatus.RESERVED);
        slotRepository.save(slot);


        Reservation reservation = new Reservation();
        reservation.setUserId(userId);
        reservation.setParkingLotId(parkingLotId);
        reservation.setParkingSlot(slot);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        reservation.setStatus(ReservationStatus.ACTIVE);

        reservation = reservationRepository.save(reservation);

        // 5️⃣ Prepare response
        ReservationResponseDto response = new ReservationResponseDto();
        response.setReservationId(reservation.getId());
        response.setSlotId(slot.getId());
        response.setParkingLotId(parkingLotId);
        response.setReservedAt(reservation.getReservedAt());
        response.setExpiresAt(reservation.getExpiresAt());

        return response;
    }

}
