package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.RaffleService;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;

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

        // OPCIÓN A: Si en tu filtro JWT guardas tu propia clase UserDetails
        // (Recomendado, asumiendo que tienes una clase Usuario o CustomUserDetails)
        /*
        if (authentication.getPrincipal() instanceof TuClaseUserDetails userDetails) {
            return userDetails.getId(); // Asegurate de que tu clase tenga el método getId()
        }
        */

        // OPCIÓN B: Si en tu filtro JWT guardas el ID o el username directamente como un String
        if (authentication.getPrincipal() instanceof String) {
            try {
                // Si guardaste el ID del usuario como un String en el principal
                return Long.parseLong((String) authentication.getPrincipal());
            } catch (NumberFormatException e) {
                // Si guardaste el username/email, tendrías que buscar el usuario en la BD primero
                // (Para esto necesitarías inyectar el UserRepository en el Controller o Service)
                throw new IllegalStateException("El principal no es un ID numérico válido");
            }
        }

        throw new IllegalStateException("No se pudo extraer el user_id del contexto de seguridad.");
    }
}
