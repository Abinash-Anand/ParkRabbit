package com.parkrabbit.backend.service;

import com.parkrabbit.backend.dto.*;
import com.parkrabbit.backend.entity.*;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;
import com.parkrabbit.backend.messaging.ParkingSessionProducer;
import com.parkrabbit.backend.repository.*;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ParkingSessionService {

    private final ParkingSessionRepository sessionRepository;
    private final ParkingSlotRepository slotRepository;
    private final ReservationRepository reservationRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingSessionProducer producer;
    private final TaskScheduler scheduler;
    private final ReservationService reservationService;

    public ParkingSessionService(
            ParkingSessionRepository sessionRepository,
            ParkingSlotRepository slotRepository,
            ReservationRepository reservationRepository,
            ParkingLotRepository parkingLotRepository,
            ParkingSessionProducer producer,
            TaskScheduler scheduler,
            ReservationService reservationService
    ) {
        this.sessionRepository = sessionRepository;
        this.slotRepository = slotRepository;
        this.reservationRepository = reservationRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.producer = producer;
        this.scheduler = scheduler;
        this.reservationService = reservationService;
    }

    /**
     * 🚗 Start parking session (1 hour fixed)
     */
    @Transactional
    public void startSession(Long reservationId) {

        // ✅ CONFIRM RESERVATION (VERY IMPORTANT)
        reservationService.confirmReservation(reservationId);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        /**
         * ❌ BLOCK session start if reservation is NOT confirmed
         */
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            throw new IllegalStateException(
                    "Parking session can only start for CONFIRMED reservations"
            );
        }

        /**
         * ❌ BLOCK session start if no slot assigned (queued users)
         */
        if (reservation.getParkingSlot() == null) {
            throw new IllegalStateException(
                    "Cannot start parking session: user is in queue"
            );
        }

        if (sessionRepository.existsByReservationIdAndStatus(
                reservationId,
                ParkingSessionStatus.ACTIVE
        )) {
            throw new IllegalStateException(
                    "Parking session already active for this reservation"
            );
        }

        ParkingSlot slot = reservation.getParkingSlot();

        // Mark slot OCCUPIED
        slot.setStatus(ParkingSlotStatus.OCCUPIED);
        slotRepository.save(slot);

        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = start.plusHours(1);

        ParkingSession session = new ParkingSession();
        session.setUserId(reservation.getUserId());
        session.setReservationId(reservationId);
        session.setParkingLotId(reservation.getParkingLotId());
        session.setSlotId(slot.getId());
        session.setStartTime(start);
        session.setEndTime(end);
        session.setStatus(ParkingSessionStatus.ACTIVE);
        sessionRepository.save(session);

        ParkingLot lot = parkingLotRepository.findById(reservation.getParkingLotId())
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        // 📣 Notify SESSION STARTED
        producer.sendUserNotification(
                new UserNotificationEvent(
                        reservation.getUserId(),
                        "SESSION_STARTED",
                        "Parking session started at "
                                + lot.getName()
                                + ", Slot " + slot.getId()
                                + ". Ends at " + end
                )
        );

        // 🔔 10-minute reminder
        scheduler.schedule(
                () -> producer.sendUserNotification(
                        new UserNotificationEvent(
                                reservation.getUserId(),
                                "SESSION_REMINDER",
                                "Your parking session will end at " + end
                        )
                ),
                end.minusMinutes(10)
                        .atZone(ZoneId.systemDefault())
                        .toInstant()
        );

        // ⛔ Auto-end after 1 hour
        scheduler.schedule(
                () -> endSession(session.getId()),
                end.atZone(ZoneId.systemDefault()).toInstant()
        );
    }

    /**
     * 🏁 End parking session
     */
    @Transactional
    public void endSession(Long sessionId) {

        ParkingSession session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        if (session.getStatus() == ParkingSessionStatus.ENDED) {
            return;
        }

        session.setStatus(ParkingSessionStatus.ENDED);
        sessionRepository.save(session);

        ParkingSlot slot = slotRepository.findById(session.getSlotId())
                .orElseThrow(() -> new RuntimeException("Slot not found"));

        slot.setStatus(ParkingSlotStatus.FREE);
        slotRepository.save(slot);

        Reservation reservation = reservationRepository.findById(session.getReservationId())
                .orElseThrow(() -> new RuntimeException("Reservation not found"));

        // 📣 Notify SESSION ENDED
        producer.sendUserNotification(
                new UserNotificationEvent(
                        reservation.getUserId(),
                        "SESSION_ENDED",
                        "Your parking session has ended. Please remove your car."
                )
        );

        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(reservation);

        // ✅ CORRECT AUTO-ASSIGN
        reservationService.assignSlotToNextQueuedUser(
                session.getParkingLotId(),
                slot
        );

    }

}
