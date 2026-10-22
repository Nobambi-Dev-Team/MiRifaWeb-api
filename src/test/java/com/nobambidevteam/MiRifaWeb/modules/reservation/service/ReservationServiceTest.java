package com.nobambidevteam.MiRifaWeb.modules.reservation.service;

import com.nobambidevteam.MiRifaWeb.exception.BusinessRuleException;
import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleStatus;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
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

    private ReservationRequestDto requestDto;

    @BeforeEach
    void setup() {
        requestDto = new ReservationRequestDto(
                10, "Cristian", "Campos", "cristiank@gmail.com", "543855678798"
        );
    }

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

    @Test
    void shouldCreateReservation_Success() {
        Long raffleId = 1L;

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setNumberCount(100);
        mockRaffle.setStatus(RaffleStatus.STARTED); // Rifa activa

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));
        when(reservationRepository.existsByRaffle_RaffleIdAndNumber(raffleId, requestDto.getNumber())).thenReturn(false);

        // Act
        reservationService.createReservation(raffleId, requestDto);

        // Assert
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void shouldThrowResourceNotFoundException_WhenRaffleDoesNotExist_InCreateReservation() {
        // Arrange
        Long raffleId = 99L;

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            reservationService.createReservation(raffleId, requestDto);
        });

        assertEquals("La rifa especificada no existe", exception.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldThrowBusinessRuleException_WhenRaffleIsNotActive() {
        // Arrange
        Long raffleId = 1L;

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setStatus(RaffleStatus.FINISHED); // Estado distinto a STARTED

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.createReservation(raffleId, requestDto);
        });

        assertEquals("Esta rifa no está activa", exception.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldThrowBusinessRuleException_WhenNumberIsOutOfBounds() {
        // Arrange
        Long raffleId = 1L;
        requestDto.setNumber(150);

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setNumberCount(100); // Rifa de 100 números (del 0 al 99)
        mockRaffle.setStatus(RaffleStatus.STARTED);

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.createReservation(raffleId, requestDto);
        });

        assertEquals("El número elegido supera la cantidad de números de la rifa", exception.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldThrowBusinessRuleException_WhenNumberIsAlreadyTaken() {
        // Arrange
        Long raffleId = 1L;

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setNumberCount(100);
        mockRaffle.setStatus(RaffleStatus.STARTED);

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));
        // Simulamos que el repositorio responde que el número ya está ocupado
        when(reservationRepository.existsByRaffle_RaffleIdAndNumber(raffleId, requestDto.getNumber())).thenReturn(true);

        // Act & Assert
        BusinessRuleException exception = assertThrows(BusinessRuleException.class, () -> {
            reservationService.createReservation(raffleId, requestDto);
        });

        assertEquals("El número 10 ya se encuentra reservado.", exception.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }
}
