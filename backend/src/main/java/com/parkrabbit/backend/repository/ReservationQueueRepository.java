package com.parkrabbit.backend.repository;

import com.parkrabbit.backend.entity.ReservationQueue;
import com.parkrabbit.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ReservationQueueRepository
        extends JpaRepository<ReservationQueue, Long> {

    /**
     * Fetch the first user in queue (FIFO) for a parking lot
     */
    Optional<ReservationQueue>
    findFirstByParkingLotIdOrderByQueuedAtAsc(Long parkingLotId);

    /**
     * Check if user is already waiting for this parking lot
     */
    boolean existsByUserAndParkingLotId(User user, Long parkingLotId);
}
