package com.nobambidevteam.MiRifaWeb.modules.reservation.service;

import com.nobambidevteam.MiRifaWeb.exception.BusinessRuleException;
import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleStatus;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.mapper.ReservationMapper;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.interfaces.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


@Service
@RequiredArgsConstructor
public class ReservationService implements IReservationService {

    private final ReservationRepository reservationRepository;
    private final RaffleRepository raffleRepository;

    @Override
    public Page<ReservationResponseDto> getReservationsByRaffle(Long raffleId, Long userId, Pageable pageable) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new ResourceNotFoundException("La rifa especificada no existe"));

        if (!raffle.getUserId().equals(userId)) {
            throw new AccessDeniedException("No tienes permisos para acceder a las reservas de esta rifa");
        }

        Page<Reservation> reservationsPage = reservationRepository.findByRaffleId(raffleId, pageable);

        return reservationsPage.map(ReservationMapper::toResponseDTO);

    }

    @Override
    @Transactional
    public void createReservation(Long raffleId, ReservationRequestDto requestDto) {
        Raffle raffle = raffleRepository.findById(raffleId)
                .orElseThrow(() -> new ResourceNotFoundException("La rifa especificada no existe"));

        if (!raffle.getStatus().equals(RaffleStatus.STARTED)) {
            throw new BusinessRuleException("Esta rifa no está activa");
        }

        if (requestDto.getNumber() >= raffle.getNumberCount()) {
            throw new BusinessRuleException("El número elegido supera la cantidad de números de la rifa");
        }

        boolean isTaken = reservationRepository.existsByRaffle_RaffleIdAndNumber(raffleId, requestDto.getNumber());
        if (isTaken) {
            throw new BusinessRuleException("El número " + requestDto.getNumber() + " ya se encuentra reservado.");
        }

        Reservation newReservation = ReservationMapper.toEntity(requestDto);
        newReservation.setRaffle(raffle);
        newReservation.setReservedAt(LocalDateTime.now());
        newReservation.setStatus(ReservationStatus.PENDING_PAYMENT);

        reservationRepository.save(newReservation);

        // TODO comunicar con MercadoPago
    }
}
