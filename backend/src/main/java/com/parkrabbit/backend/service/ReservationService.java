package com.parkrabbit.backend.service;

import com.parkrabbit.backend.dto.ReservationResponseDto;
import com.parkrabbit.backend.dto.UserNotificationEvent;
import com.parkrabbit.backend.entity.*;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;
import com.parkrabbit.backend.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.parkrabbit.backend.messaging.ParkingSessionProducer;

import java.time.LocalDateTime;

@Service
public class ReservationService {

    private final ParkingSlotRepository slotRepository;
    private final ReservationRepository reservationRepository;
    private final ReservationQueueRepository queueRepository;
    private final UserRepository userRepository;
    private final ParkingLotRepository parkingLotRepository;

    private final ParkingSessionProducer producer;

    public ReservationService(
            ParkingSlotRepository slotRepository,
            ReservationRepository reservationRepository,
            ReservationQueueRepository queueRepository,
            UserRepository userRepository,
            ParkingLotRepository parkingLotRepository,
            ParkingSessionProducer producer
    ) {
        this.slotRepository = slotRepository;
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.userRepository = userRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.producer = producer;
    }

    @Transactional
    public ReservationResponseDto reserveSlot(Long parkingLotId, Long userId) {

        //  One active reservation per user
        if (reservationRepository.existsByUserIdAndStatus(userId, ReservationStatus.ACTIVE)) {
            throw new IllegalStateException("User already has an active reservation");
        }

        //  Load required entities
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        ParkingLot parkingLot = parkingLotRepository.findById(parkingLotId)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        //  Try to find FREE slot in REQUESTED parking lot
        ParkingSlot slot = slotRepository
                .findFirstByParkingLotIdAndStatus(parkingLotId, ParkingSlotStatus.FREE)
                .orElse(null);

        //  NO SLOT → ADD TO QUEUE
        if (slot == null) {

            // prevent duplicate queue entry
            if (!queueRepository.existsByUserAndParkingLotId(user, parkingLotId)) {
                ReservationQueue queueEntry =
                        new ReservationQueue(user, parkingLotId);
                queueRepository.save(queueEntry);
            }

            ReservationResponseDto response = new ReservationResponseDto();
            response.setQueued(true);
            response.setMessage("All slots full. User added to queue.");

            response.setParkingLotId(parkingLot.getId());
            response.setParkingLotName(parkingLot.getName());
            response.setParkingLotAddress(parkingLot.getAddress());

            return response;
        }

        //  SLOT FOUND → RESERVE
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

        //  Response
        ReservationResponseDto response = new ReservationResponseDto();
        response.setReservationId(reservation.getId());
        response.setSlotId(slot.getId());
        response.setParkingLotId(parkingLotId);
        response.setParkingLotName(parkingLot.getName());
        response.setParkingLotAddress(parkingLot.getAddress());
        response.setReservedAt(reservation.getReservedAt());
        response.setExpiresAt(reservation.getExpiresAt());
        response.setQueued(false);
        response.setMessage("Slot reserved successfully");

        return response;
    }

    @Transactional(readOnly = true)
    public ReservationResponseDto getActiveReservationForUser(Long userId) {

        return reservationRepository
                .findByUserIdAndStatus(userId, ReservationStatus.ACTIVE)
                .map(reservation -> {

                    ParkingLot lot = parkingLotRepository
                            .findById(reservation.getParkingLotId())
                            .orElseThrow();

                    ReservationResponseDto dto = new ReservationResponseDto();
                    dto.setReservationId(reservation.getId());
                    dto.setSlotId(reservation.getParkingSlot().getId());
                    dto.setParkingLotId(lot.getId());
                    dto.setParkingLotName(lot.getName());
                    dto.setParkingLotAddress(lot.getAddress());
                    dto.setReservedAt(reservation.getReservedAt());
                    dto.setExpiresAt(reservation.getExpiresAt());
                    dto.setQueued(false);
                    dto.setMessage("Active reservation found");

                    return dto;
                })
                .orElseGet(() -> {
                    ReservationResponseDto dto = new ReservationResponseDto();
                    dto.setQueued(false);
                    dto.setMessage("No active reservation");
                    return dto;
                });
    }


    @Transactional
    public void confirmReservation(Long reservationId) {

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // ❌ EXPIRED
        if (reservation.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Reservation expired");
        }

        // ❌ NO SLOT (QUEUED USER)
        if (reservation.getParkingSlot() == null) {
            throw new IllegalStateException("Queued reservation cannot be confirmed");
        }

        // ✅ prevent double confirm
        if (reservation.getStatus() == ReservationStatus.CONFIRMED) {
            return;
        }

        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }


    @Transactional
    public void assignSlotToNextQueuedUser(Long parkingLotId, ParkingSlot freeSlot) {

        ReservationQueue nextInQueue =
                queueRepository.findFirstByParkingLotIdOrderByQueuedAtAsc(parkingLotId)
                        .orElse(null);

        if (nextInQueue == null) {
            // No queued users → slot remains FREE
            return;
        }

        User user = nextInQueue.getUser();

        // Assign slot
        freeSlot.setStatus(ParkingSlotStatus.RESERVED);
        slotRepository.save(freeSlot);

        Reservation reservation = new Reservation();
        reservation.setUserId(user.getId());
        reservation.setParkingLotId(parkingLotId);
        reservation.setParkingSlot(freeSlot);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        reservation.setStatus(ReservationStatus.ACTIVE);

        reservationRepository.save(reservation);

        producer.sendUserNotification(
                new UserNotificationEvent(
                        user.getId(),
                        "SLOT_ASSIGNED",
                        "A parking slot is reserved for you. Please confirm within 10 minutes."
                )
        );

        // Remove user from queue
        queueRepository.delete(nextInQueue);
    }

}