package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.mapper.RaffleMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import com.nobambidevteam.MiRifaWeb.exception.ResourceNotFoundException;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.OccupiedNumberDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleNumbersStatusResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
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
}
