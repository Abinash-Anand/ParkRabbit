package com.parkrabbit.backend.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.parkrabbit.backend.dto.ParkingLotAvailabilityResponse;
import com.parkrabbit.backend.dto.ParkingLotCreateRequest;
import com.parkrabbit.backend.dto.ParkingLotResponse;
import com.parkrabbit.backend.entity.ParkingLot;
import com.parkrabbit.backend.mapper.ParkingLotMapper;
import com.parkrabbit.backend.repository.ParkingLotRepository;
import com.parkrabbit.backend.repository.ParkingSlotRepository;

@Service
public class ParkingLotService {
    private final ParkingLotRepository parkingLotRepository;
    private final ParkingLotMapper parkingLotMapper;
    private final ParkingSlotRepository parkingSlotRepository;

    public ParkingLotService(
            ParkingLotRepository parkingLotRepository,
            ParkingLotMapper parkingLotMapper,
            ParkingSlotRepository parkingSlotRepository

    ) {
        this.parkingLotRepository = parkingLotRepository;
        this.parkingLotMapper = parkingLotMapper;
        this.parkingSlotRepository = parkingSlotRepository;

    }
    // // Get all parking lots
    // public List<ParkingLot> getAllParkingLots(){
    // return parkingLotRepository.findAll();
    // }

    // create parking lots
    public ParkingLotResponse createParkingLot(ParkingLotCreateRequest request) {
        ParkingLot entity = parkingLotMapper.toEntity(request);
        ParkingLot saved = parkingLotRepository.save(entity);

        return parkingLotMapper.toResponse(saved);
    }

    // getAllParkingLotsWithAvailability
    public List<ParkingLotAvailabilityResponse> getAllParkingLotsWithAvailability() {

        List<ParkingLot> lots = parkingLotRepository.findAll();
        List<ParkingLotAvailabilityResponse> responseList = new ArrayList<>();

        for (ParkingLot lot : lots) {
            List<Object[]> result = parkingSlotRepository.countSlotAvailability(lot.getId());

            Object[] counts = result.get(0);

            ParkingLotAvailabilityResponse dto = new ParkingLotAvailabilityResponse();
            dto.setId(lot.getId());
            dto.setName(lot.getName());
            dto.setAddress(lot.getAddress());
            dto.setLatitude(lot.getLatitude());
            dto.setLongitude(lot.getLongitude());
            dto.setHourlyRate(lot.getHourlyRate());
            dto.setTotalSlots(lot.getTotalSlots());

            dto.setFreeSlots(((Number) counts[0]).longValue());
            dto.setReservedSlots(((Number) counts[1]).longValue());
            dto.setOccupiedSlots(((Number) counts[2]).longValue());

            responseList.add(dto);
        }

        return responseList;
    }

    // GET oarking lot by userId
    public ParkingLotAvailabilityResponse getParkingLotById(Long id) {

        ParkingLot lot = parkingLotRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Parking lot not found"));

        List<Object[]> result = parkingSlotRepository.countSlotAvailability(lot.getId());

        Object[] counts = result.get(0);
        ParkingLotAvailabilityResponse dto = new ParkingLotAvailabilityResponse();
        dto.setId(lot.getId());
        dto.setName(lot.getName());
        dto.setAddress(lot.getAddress());
        dto.setLatitude(lot.getLatitude());
        dto.setLongitude(lot.getLongitude());
        dto.setHourlyRate(lot.getHourlyRate());
        dto.setTotalSlots(lot.getTotalSlots());

        dto.setFreeSlots(((Number) counts[0]).longValue());
        dto.setReservedSlots(((Number) counts[1]).longValue());
        dto.setOccupiedSlots(((Number) counts[2]).longValue());

        return dto;
    }

}
