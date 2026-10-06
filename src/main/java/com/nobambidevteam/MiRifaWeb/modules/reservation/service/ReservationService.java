package com.nobambidevteam.MiRifaWeb.modules.reservation.service;

import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.mapper.ReservationMapper;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.interfaces.IReservationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;


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
}
