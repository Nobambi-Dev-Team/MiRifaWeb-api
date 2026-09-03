package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.RaffleService;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import com.nobambidevteam.MiRifaWeb.security.principal.CustomUserPrincipal;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleNumbersStatusResponseDto;

@RestController
@RequestMapping("/api/raffles")
@RequiredArgsConstructor
public class RaffleController {

    private final IRaffleService raffleService;

    @PostMapping
    public ResponseEntity<RaffleResponseDto> createRaffle(
            @Valid @RequestBody RaffleRequestDto requestDTO,
            Authentication authentication) {

        // Extraemos el user_id del token JWT en la sesión actual
        Long userId = extractUserIdFromAuthentication(authentication);

        RaffleResponseDto responseDTO = raffleService.createRaffle(requestDTO, userId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    /**
     * Método utilitario para extraer el user_id de la sesión autenticada.
     */
    private Long extractUserIdFromAuthentication(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserPrincipal customPrincipal) {
            return customPrincipal.id();
        }

        throw new IllegalStateException("No se pudo extraer el user_id del contexto de seguridad.");
    }

    @GetMapping("/{raffle_id}/numbers-status")
    public ResponseEntity<RaffleNumbersStatusResponseDto> getNumbersStatus(
            @PathVariable("raffle_id") Long raffleId) {

        RaffleNumbersStatusResponseDto response = raffleService.getNumbersStatus(raffleId);

        return ResponseEntity.ok(response); // Retorna 200 OK por defecto
    }
}
