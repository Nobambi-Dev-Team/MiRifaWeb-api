package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.Category;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RaffleServiceTest {

    @Mock
    private RaffleRepository raffleRepository;

    @InjectMocks
    private RaffleService raffleService;

    private RaffleRequestDto requestDTO;

    @BeforeEach
    void setUp() {
        PrizeRequestDto prize = new PrizeRequestDto(1, "Canasta familiar");
        requestDTO = new RaffleRequestDto(
                "Sorteo Test",
                "Descripción test",
                100,
                new BigDecimal("1500.50"),
                "mi.alias.mp",
                LocalDateTime.now().plusMonths(1),
                Category.VIAJES,
                "url_imagen",
                List.of(prize)
        );
    }

    @Test
    void shouldCreateRaffleSuccessfully() {
        // Arrange
        Long userId = 1L;

        // Simulamos lo que devolvería la base de datos al guardar
        Raffle savedRaffle = new Raffle();
        savedRaffle.setRaffleId(100L);
        savedRaffle.setUserId(userId);
        savedRaffle.setTitle(requestDTO.getTitle());
        savedRaffle.setStartDate(LocalDateTime.now());

        when(raffleRepository.save(any(Raffle.class))).thenReturn(savedRaffle);

        // Act
        RaffleResponseDto response = raffleService.createRaffle(requestDTO, userId);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Sorteo Test", response.getTitle());
        assertNotNull(response.getStartDate());

        verify(raffleRepository, times(1)).save(any(Raffle.class));
    }
}
