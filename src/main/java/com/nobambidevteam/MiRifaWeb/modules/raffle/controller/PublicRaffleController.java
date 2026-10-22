package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/raffles")
@RequiredArgsConstructor
public class PublicRaffleController {

    private final IRaffleService raffleService;

    @GetMapping("/{raffle_id}")
    public ResponseEntity<RaffleResponseDto> getPublicRaffle(
            @PathVariable("raffle_id") Long raffleId) {

        RaffleResponseDto response = raffleService.getPublicRaffleDetails(raffleId);

        return ResponseEntity.ok(response);
    }
}