package com.nobambidevteam.MiRifaWeb.modules.reservation.service.interfaces;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IReservationService {
    Page<ReservationResponseDto> getReservationsByRaffle(Long raffleId, Long userId, Pageable pageable);
}
