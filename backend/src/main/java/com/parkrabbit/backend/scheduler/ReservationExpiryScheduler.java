package com.parkrabbit.backend.scheduler;

import com.parkrabbit.backend.entity.ParkingSlot;
import com.parkrabbit.backend.entity.Reservation;
import com.parkrabbit.backend.entity.ReservationStatus;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;
import com.parkrabbit.backend.messaging.ReservationExpiredProducer;
import com.parkrabbit.backend.repository.ReservationRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class ReservationExpiryScheduler {

    private final ReservationRepository reservationRepository;
    private final ReservationExpiredProducer expiredProducer;


    public ReservationExpiryScheduler(
            ReservationRepository reservationRepository,
            ReservationExpiredProducer expiredProducer
    ) {
        this.reservationRepository = reservationRepository;
        this.expiredProducer = expiredProducer;
    }

    /**
     *  THIS RUNS AUTOMATICALLY EVERY 60 SECONDS
     */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void expireReservations() {

        // 1️ Find expired ACTIVE reservations
        List<Reservation> expiredReservations =
                reservationRepository.findByStatusAndExpiresAtBefore(
                        ReservationStatus.ACTIVE,
                        LocalDateTime.now()
                );

        for (Reservation reservation : expiredReservations) {

            // 2️ Mark reservation as EXPIRED
            reservation.setStatus(ReservationStatus.EXPIRED);
            expiredProducer.sendExpiredReservation(reservation.getId());


            // 3️ FREE the parking slot (THIS IS WHAT YOU ASKED ABOUT)
            ParkingSlot slot = reservation.getParkingSlot();
            slot.setStatus(ParkingSlotStatus.FREE);
        }
        // Hibernate auto-saves because of @Transactional
    }
}
