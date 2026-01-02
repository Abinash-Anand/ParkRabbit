package com.parkrabbit.backend.repository;

import com.parkrabbit.backend.entity.Reservation;
import com.parkrabbit.backend.entity.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByStatusAndExpiresAtBefore(
            ReservationStatus status,
            LocalDateTime time
    );
    boolean existsByUserIdAndStatus(Long userId, ReservationStatus status);
    
    Optional<Reservation> findByUserIdAndStatus(Long userId, ReservationStatus status);
}
