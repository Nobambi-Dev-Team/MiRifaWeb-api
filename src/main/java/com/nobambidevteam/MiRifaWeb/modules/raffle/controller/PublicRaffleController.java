package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.ReservationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/raffles")
@RequiredArgsConstructor
public class PublicRaffleController {

    private final ReservationService reservationService;

    @PostMapping("/{raffle_id}/reservations")
    public ResponseEntity<Void> createReservation(
            @PathVariable("raffle_id") Long raffleId,
            @Valid @RequestBody ReservationRequestDto requestDto) {

        reservationService.createReservation(raffleId, requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
