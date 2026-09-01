package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
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

import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.OccupiedNumberDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleNumbersStatusResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class RaffleServiceTest {

    @Mock
    private RaffleRepository raffleRepository;

    @Mock
    private ReservationRepository reservationRepository;

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
                RaffleCategory.TRAVEL,
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

    @Test
    void shouldReturnNumbersStatusSuccessfully() {
        // Arrange
        Long raffleId = 1L;

        // Simulamos la rifa que se encuentra en la base de datos
        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setNumberCount(100); // total_capacity

        // Simulamos la lista de números ocupados
        List<OccupiedNumberDto> mockOccupiedNumbers = List.of(
                new OccupiedNumberDto(7, ReservationStatus.RESERVED),
                new OccupiedNumberDto(10, ReservationStatus.RESERVED)
        );

        // Configuramos los mocks
        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));
        when(reservationRepository.findOccupiedNumbersByRaffleId(raffleId)).thenReturn(mockOccupiedNumbers);

        // Act
        RaffleNumbersStatusResponseDto response = raffleService.getNumbersStatus(raffleId);

        // Assert
        assertNotNull(response);
        assertEquals(100, response.getTotalCapacity());
        assertEquals(2, response.getOccupiedNumbers().size());
        assertEquals(7, response.getOccupiedNumbers().get(0).getNumber());
        assertEquals(ReservationStatus.RESERVED, response.getOccupiedNumbers().get(0).getStatus());

        // Verificamos que los repositorios fueron llamados exactamente una vez
        verify(raffleRepository, times(1)).findById(raffleId);
        verify(reservationRepository, times(1)).findOccupiedNumbersByRaffleId(raffleId);
    }

    @Test
    void shouldThrowExceptionWhenRaffleNotFoundOnNumbersStatus() {
        // Arrange
        Long invalidRaffleId = 99L;

        // Simulamos que el repositorio no encuentra nada
        when(raffleRepository.findById(invalidRaffleId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            raffleService.getNumbersStatus(invalidRaffleId);
        });

        // Verificamos que si falla la búsqueda de la rifa, NO se hace la consulta de reservas (optimización)
        verify(raffleRepository, times(1)).findById(invalidRaffleId);
        verify(reservationRepository, never()).findOccupiedNumbersByRaffleId(anyLong());
    }
}
