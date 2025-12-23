package com.parkrabbit.backend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.parkrabbit.backend.entity.ParkingSlot;
import com.parkrabbit.backend.entity.enums.ParkingSlotStatus;

public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    Optional<ParkingSlot> findFirstByParkingLotIdAndStatus(
            Long parkingLotId,
            ParkingSlotStatus status);

    // ------------- Aggregation Query --------
    @Query("""
                SELECT
                    SUM(CASE WHEN s.status = 'FREE' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN s.status = 'RESERVED' THEN 1 ELSE 0 END),
                    SUM(CASE WHEN s.status = 'OCCUPIED' THEN 1 ELSE 0 END)
                FROM ParkingSlot s
                WHERE s.parkingLot.id = :lotId
            """)
    List<Object[]> countSlotAvailability(@Param("lotId") Long lotId);

}
