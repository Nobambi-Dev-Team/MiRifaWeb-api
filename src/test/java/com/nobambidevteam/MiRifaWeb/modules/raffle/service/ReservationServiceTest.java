package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.ReservationService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private RaffleRepository raffleRepository;

    @InjectMocks
    private ReservationService reservationService;

    @Test
    void shouldGetReservationsByRaffle_Success() {
        // Arrange
        Long raffleId = 1L;
        Long ownerUserId = 100L;
        Pageable pageable = PageRequest.of(0, 20);

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setUserId(ownerUserId);

        Reservation mockReservation = new Reservation();
        mockReservation.setReservationId(10L);
        mockReservation.setNumber(5);
        mockReservation.setBuyerName("Cristian");

        Page<Reservation> mockPage = new PageImpl<>(List.of(mockReservation));

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));
        when(reservationRepository.findByRaffleId(eq(raffleId), any(Pageable.class))).thenReturn(mockPage);

        // Act
        Page<ReservationResponseDto> result = reservationService.getReservationsByRaffle(raffleId, ownerUserId, pageable);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Cristian", result.getContent().get(0).getBuyerName());

        verify(raffleRepository, times(1)).findById(raffleId);
        verify(reservationRepository, times(1)).findByRaffleId(raffleId, pageable);
    }

    @Test
    void shouldThrowEntityNotFoundException_WhenRaffleDoesNotExist() {
        // Arrange
        Long invalidRaffleId = 99L;
        Long userId = 100L;
        Pageable pageable = PageRequest.of(0, 20);

        when(raffleRepository.findById(invalidRaffleId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.getReservationsByRaffle(invalidRaffleId, userId, pageable);
        });

        assertEquals("La rifa especificada no existe", exception.getMessage());

        verify(reservationRepository, never()).findByRaffleId(any(), any());
    }

    @Test
    void shouldThrowAccessDeniedException_WhenUserIsNotTheOwner() {
        // Arrange
        Long raffleId = 1L;
        Long ownerUserId = 100L;
        Long hackerUserId = 999L;
        Pageable pageable = PageRequest.of(0, 20);

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setUserId(ownerUserId);

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));

        // Act & Assert
        AccessDeniedException exception = assertThrows(AccessDeniedException.class, () -> {
            reservationService.getReservationsByRaffle(raffleId, hackerUserId, pageable);
        });

        assertEquals("No tienes permisos para acceder a las reservas de esta rifa", exception.getMessage());

        // Verificamos que se bloqueó el acceso antes de consultar los datos sensibles
        verify(reservationRepository, never()).findByRaffleId(any(), any());
    }
}
