package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.exception.BusinessRuleException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.*;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleStatus;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
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
        Long userId = 1L;

        Raffle savedRaffle = new Raffle();
        savedRaffle.setRaffleId(100L);
        savedRaffle.setUserId(userId);
        savedRaffle.setTitle(requestDTO.getTitle());
        savedRaffle.setStartDate(LocalDateTime.now());

        when(raffleRepository.save(any(Raffle.class))).thenReturn(savedRaffle);

        RaffleResponseDto response = raffleService.createRaffle(requestDTO, userId);

        assertNotNull(response);
        assertEquals(100L, response.getId());
        assertEquals("Sorteo Test", response.getTitle());
        assertNotNull(response.getStartDate());

        verify(raffleRepository, times(1)).save(any(Raffle.class));
    }

    @Test
    void shouldReturnNumbersStatusSuccessfully() {
        Long raffleId = 1L;

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setNumberCount(100);

        List<OccupiedNumberDto> mockOccupiedNumbers = List.of(
                new OccupiedNumberDto(7, ReservationStatus.RESERVED),
                new OccupiedNumberDto(10, ReservationStatus.RESERVED)
        );

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));
        when(reservationRepository.findOccupiedNumbersByRaffleId(raffleId)).thenReturn(mockOccupiedNumbers);

        RaffleNumbersStatusResponseDto response = raffleService.getNumbersStatus(raffleId);

        assertNotNull(response);
        assertEquals(100, response.getTotalCapacity());
        assertEquals(2, response.getOccupiedNumbers().size());
        assertEquals(7, response.getOccupiedNumbers().get(0).getNumber());
        assertEquals(ReservationStatus.RESERVED, response.getOccupiedNumbers().get(0).getStatus());

        verify(raffleRepository, times(1)).findById(raffleId);
        verify(reservationRepository, times(1)).findOccupiedNumbersByRaffleId(raffleId);
    }

    @Test
    void shouldThrowExceptionWhenRaffleNotFoundOnNumbersStatus() {
        Long invalidRaffleId = 99L;

        when(raffleRepository.findById(invalidRaffleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            raffleService.getNumbersStatus(invalidRaffleId);
        });

        verify(raffleRepository, times(1)).findById(invalidRaffleId);
        verify(reservationRepository, never()).findOccupiedNumbersByRaffleId(anyLong());
    }

    //--------------------------- Tests from feat-get-raffle branch

    @Test
    void shouldReturnRaffleAndMetricsSuccessfully() {
        Long validRaffleId = 1L;
        Long validUserId = 1L;

        Raffle raffle = Raffle.builder()
                .raffleId(validRaffleId)
                .userId(validUserId)
                .title("Viaje de egresados 6to")
                .description("Rifa para pagarle el viaje al Santi")
                .numberCount(50)
                .unitPrice(new BigDecimal("3000.00"))
                .aliasCbu("titineta.mp")
                .startDate(LocalDateTime.now())
                .endDate(LocalDateTime.now().plusMonths(1))
                .category(RaffleCategory.TRAVEL)
                .status(RaffleStatus.STARTED)
                .imageUrl("imagen_url")
                .prizes(new ArrayList<>())
                .build();

        Reservation res1 = new Reservation(); res1.setStatus(ReservationStatus.RESERVED);
        Reservation res2 = new Reservation(); res2.setStatus(ReservationStatus.RESERVED);
        Reservation res3 = new Reservation(); res3.setStatus(ReservationStatus.PENDING_PAYMENT);
        List<Reservation> reservations = List.of(res1, res2, res3);

        when(raffleRepository.findById(validRaffleId)).thenReturn(Optional.of(raffle));
        when(reservationRepository.findAllByRaffleId(validRaffleId)).thenReturn(reservations);

        RaffleMetricsDto response = raffleService.getRaffleMetricsById(validRaffleId, validUserId);

        assertNotNull(response);
        assertNotNull(response.raffle());
        assertEquals(validRaffleId, response.raffle().getId());
        assertEquals("Viaje de egresados 6to", response.raffle().getTitle());
        assertEquals(3, response.reservedNumbersCount());
        assertEquals(4.0, response.completionPercentage());
        assertEquals(0, new BigDecimal("6000").compareTo(response.totalAmountRaised()));

        verify(raffleRepository, times(1)).findById(validRaffleId);
        verify(reservationRepository, times(1)).findAllByRaffleId(validRaffleId);
    }

    @Test
    void shouldThrowExceptionWhenRaffleNotFoundForMetrics() {
        Long invalidRaffleId = 999L;
        Long validUserId = 1L;

        when(raffleRepository.findById(invalidRaffleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            raffleService.getRaffleMetricsById(invalidRaffleId, validUserId);
        });

        verify(raffleRepository, times(1)).findById(invalidRaffleId);
        verify(reservationRepository, never()).findAllByRaffleId(anyLong());
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotTheOwnerOfRaffle() {
        Long validRaffleId = 1L;
        Long ownerUserId = 1L;
        Long nonOwnerUserId = 2L;

        Raffle raffle = Raffle.builder()
                .raffleId(validRaffleId)
                .userId(ownerUserId)
                .build();

        when(raffleRepository.findById(validRaffleId)).thenReturn(Optional.of(raffle));

        assertThrows(BusinessRuleException.class, () -> {
            raffleService.getRaffleMetricsById(validRaffleId, nonOwnerUserId);
        });

        verify(raffleRepository, times(1)).findById(validRaffleId);
        verify(reservationRepository, never()).findAllByRaffleId(anyLong());
    }

    //--------------------------- Tests from develop-branch

    @Test
    void shouldReturnPublicRaffleDetailsSuccessfully() {
        Long raffleId = 1L;

        Raffle mockRaffle = new Raffle();
        mockRaffle.setRaffleId(raffleId);
        mockRaffle.setTitle("Sorteo de fin de año");
        mockRaffle.setDescription("Recaudación para el viaje de estudios");
        mockRaffle.setNumberCount(100);
        mockRaffle.setUnitPrice(new BigDecimal("1500.50"));
        mockRaffle.setCategory(RaffleCategory.TRAVEL);

        when(raffleRepository.findById(raffleId)).thenReturn(Optional.of(mockRaffle));

        RaffleResponseDto response = raffleService.getPublicRaffleDetails(raffleId);

        assertNotNull(response);
        assertEquals(raffleId, response.getId());
        assertEquals("Sorteo de fin de año", response.getTitle());
        assertEquals(100, response.getNumberCount());

        verify(raffleRepository, times(1)).findById(raffleId);
    }

    @Test
    void shouldThrowExceptionWhenRaffleNotFoundOnPublicDetails() {
        Long invalidRaffleId = 99L;

        when(raffleRepository.findById(invalidRaffleId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            raffleService.getPublicRaffleDetails(invalidRaffleId);
        });

        verify(raffleRepository, times(1)).findById(invalidRaffleId);
    }
}