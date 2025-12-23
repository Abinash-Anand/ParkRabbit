package com.parkrabbit.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.parkrabbit.backend.entity.ParkingLot;
import com.parkrabbit.backend.service.ParkingLotService;

import jakarta.validation.Valid;

import com.parkrabbit.backend.dto.ParkingLotAvailabilityResponse;
import com.parkrabbit.backend.dto.ParkingLotCreateRequest;
import com.parkrabbit.backend.dto.ParkingLotResponse;

@RestController
@RequestMapping("/api/parking-lots")
public class ParkingLotController {
    private final ParkingLotService parkingLotService;

    public ParkingLotController(ParkingLotService parkingLotService) {
        this.parkingLotService = parkingLotService;
    }

    @GetMapping
    public ResponseEntity<List<ParkingLotAvailabilityResponse>> getAllParkingLots() {
        return ResponseEntity.ok(
                parkingLotService.getAllParkingLotsWithAvailability());
    }

      @GetMapping("/{id}")
    public ResponseEntity<ParkingLotAvailabilityResponse> getParkingLot(@PathVariable Long id) {
        return ResponseEntity.ok(
                parkingLotService.getParkingLotById(id));
    }

    @PostMapping
    public ParkingLotResponse createParkingLot(
            @Valid @RequestBody ParkingLotCreateRequest request) {
        return parkingLotService.createParkingLot(request);
    }

}
