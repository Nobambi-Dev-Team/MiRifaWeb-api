package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.exception.BusinessRuleException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.*;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.mapper.RaffleMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RaffleService implements IRaffleService {

    private final RaffleRepository raffleRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public RaffleResponseDto createRaffle(RaffleRequestDto requestDTO, Long userId) {

        Raffle newRaffle = RaffleMapper.toEntity(requestDTO, userId);

        Raffle savedRaffle = raffleRepository.save(newRaffle);

        return RaffleMapper.toResponseDTO(savedRaffle);
    }

    @Transactional(readOnly = true) // Importante para optimizar consultas de solo lectura
    @Override
    public RaffleNumbersStatusResponseDto getNumbersStatus(Long raffleId) {

        // 1. Buscamos la rifa para obtener su capacidad (y validamos que exista)
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new ResourceNotFoundException("Rifa no encontrada con el ID: " + raffleId));

        // 2. Buscamos únicamente los números ocupados y su estado
        List<OccupiedNumberDto> occupiedNumbers = reservationRepository.findOccupiedNumbersByRaffleId(raffleId);

        // 3. Ensamblamos y retornamos el DTO
        return RaffleNumbersStatusResponseDto.builder()
                .totalCapacity(raffle.getNumberCount())
                .occupiedNumbers(occupiedNumbers)
                .build();
    }

    //----------------------------------GetRaffleMetricsById
    @Override
    public RaffleMetricsDto getRaffleMetricsById(Long raffleId, Long userId) {

        // Obtener la rifa y verificar propiedad
        Raffle raffle = getRaffleByIdAndValidateOwnership(raffleId, userId);

        // Obtener reservas asociadas
        List<Reservation> reservations = reservationRepository.findAllByRaffleId(raffleId);

        // Construir y retornar el DTO con las métricas calculadas
        return buildMetricsDto(raffle, reservations);
    }

    /**
     * Busca la rifa y valida que pertenezca al usuario solicitante.
     */
    private Raffle getRaffleByIdAndValidateOwnership(Long raffleId, Long userId) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró la rifa con id " + raffleId));

        if (!raffle.getUserId().equals(userId)) {
            throw new BusinessRuleException("No eres el propietario de esta rifa");
        }

        return raffle;
    }

    /**
     * Orquesta el cálculo de métricas y la construcción del DTO de respuesta.
     */
    private RaffleMetricsDto buildMetricsDto(Raffle raffle, List<Reservation> reservations) {
        // Total de números ocupados (pendientes + pagados)
        int reservedNumbersCount = reservations.size();

        // Solo tenemos en cuenta reservas pagadas para las métricas financieras y de avance
        long paidReservationsCount = reservations.stream()
                .filter(r -> r.getStatus() == ReservationStatus.RESERVED)
                .count();

        BigDecimal totalAmountRaised = calculateTotalAmount(raffle.getUnitPrice(), paidReservationsCount);
        double completionPercentage = calculatePercentage(paidReservationsCount, raffle.getNumberCount());

        return new RaffleMetricsDto(
                RaffleMapper.toResponseDTO(raffle),
                reservedNumbersCount,
                completionPercentage,
                totalAmountRaised
        );
    }

    /**
     * Calcula el monto total recaudado.
     */
    private BigDecimal calculateTotalAmount(BigDecimal unitPrice, long paidCount) {
        if (unitPrice == null) {
            return BigDecimal.ZERO;
        }

        return unitPrice.multiply(BigDecimal.valueOf(paidCount));
    }

    /**
     * Calcula el porcentaje de avance redondeado a dos decimales.
     */
    private double calculatePercentage(long paidCount, Integer totalNumbers) {
        if (totalNumbers == null || totalNumbers <= 0) {
            return 0.0;
        }
        double percentage = ((double) paidCount / totalNumbers) * 100.0;
        return Math.round(percentage * 100.0) / 100.0;
    }
}
