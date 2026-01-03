package com.parkrabbit.backend.scheduler;

import com.parkrabbit.backend.dto.ReservationExpiredEvent;
import com.parkrabbit.backend.dto.SlotAutoAssignedEvent;
import com.parkrabbit.backend.entity.*;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;
import com.parkrabbit.backend.messaging.ReservationExpiredProducer;
import com.parkrabbit.backend.messaging.SlotAutoAssignedProducer;
import com.parkrabbit.backend.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ReservationExpiryScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationQueueRepository queueRepository;
    private final ParkingLotRepository parkingLotRepository;
    private final ReservationExpiredProducer expiredProducer;
    private final SlotAutoAssignedProducer slotAutoAssignedProducer;

    public ReservationExpiryScheduler(
            ReservationRepository reservationRepository,
            ReservationQueueRepository queueRepository,
            ParkingLotRepository parkingLotRepository,
            ReservationExpiredProducer expiredProducer,
            SlotAutoAssignedProducer slotAutoAssignedProducer
    ) {
        this.reservationRepository = reservationRepository;
        this.queueRepository = queueRepository;
        this.parkingLotRepository = parkingLotRepository;
        this.expiredProducer = expiredProducer;
        this.slotAutoAssignedProducer = slotAutoAssignedProducer;
    }

    @Scheduled(fixedRate = 10000)
    @Transactional
    public void expireReservations() {

        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndExpiresAtBefore(
                        ReservationStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (Reservation reservation : expiredReservations) {

            reservation.setStatus(ReservationStatus.EXPIRED);

            ParkingLot parkingLot =
                    parkingLotRepository.findById(reservation.getParkingLotId())
                            .orElseThrow();

            // 🔔 Notify expired user
            expiredProducer.sendExpiredReservationEvent(
                    new ReservationExpiredEvent(
                            reservation.getId(),
                            reservation.getUserId(),
                            reservation.getParkingLotId(),
                            reservation.getParkingSlot().getId(),
                            LocalDateTime.now(),
                            parkingLot.getName(),
                            parkingLot.getAddress()
                    )
            );

            ParkingSlot slot = reservation.getParkingSlot();

            Optional<ReservationQueue> queueOpt =
                    queueRepository.findFirstByParkingLotIdOrderByQueuedAtAsc(
                            reservation.getParkingLotId()
                    );

            if (queueOpt.isPresent()) {
                ReservationQueue queue = queueOpt.get();

                Reservation newReservation = new Reservation();
                newReservation.setUserId(queue.getUser().getId());
                newReservation.setParkingLotId(reservation.getParkingLotId());
                newReservation.setParkingSlot(slot);
                newReservation.setReservedAt(LocalDateTime.now());
                newReservation.setExpiresAt(LocalDateTime.now().plusMinutes(1));
                newReservation.setStatus(ReservationStatus.ACTIVE);

                slot.setStatus(ParkingSlotStatus.RESERVED);

                reservationRepository.save(newReservation);
                queueRepository.delete(queue);

                // 🔔 Notify queued user
                slotAutoAssignedProducer.sendSlotAutoAssignedEvent(
                        new SlotAutoAssignedEvent(
                                queue.getUser().getId(),
                                newReservation.getId(),
                                reservation.getParkingLotId(),
                                slot.getId(),
                                newReservation.getReservedAt(),
                                parkingLot.getName(),
                                parkingLot.getAddress()
                        )
                );

                System.out.println(
                        "Slot reassigned to queued user: " +
                                queue.getUser().getUsername()
                );
            } else {
                slot.setStatus(ParkingSlotStatus.FREE);
            }
        }
    }
}
