package com.nobambidevteam.MiRifaWeb.modules.raffle.service;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.mapper.RaffleMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RaffleService implements IRaffleService {

    private final RaffleRepository raffleRepository;

    @Transactional
    public RaffleResponseDto createRaffle(RaffleRequestDto requestDTO, Long userId) {

        Raffle newRaffle = RaffleMapper.toEntity(requestDTO, userId);

        Raffle savedRaffle = raffleRepository.save(newRaffle);

        return RaffleMapper.toResponseDTO(savedRaffle);
    }
}
