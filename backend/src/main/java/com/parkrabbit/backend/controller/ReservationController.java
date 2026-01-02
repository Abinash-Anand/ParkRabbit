package com.parkrabbit.backend.controller;

import com.parkrabbit.backend.dto.ReservationRequestDto;
import com.parkrabbit.backend.dto.ReservationResponseDto;
import com.parkrabbit.backend.entity.User;
import com.parkrabbit.backend.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponseDto> reserveSlot(
            @RequestBody ReservationRequestDto requestDto
    ) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        User user = (User) authentication.getPrincipal();

        ReservationResponseDto response =
                reservationService.reserveSlot(
                        requestDto.getParkingLotId(),
                        user.getId()
                );

        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ReservationResponseDto> getMyReservation(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        return ResponseEntity.ok(reservationService.getActiveReservationForUser(user.getId()));
    }
}
