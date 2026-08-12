package com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;

public interface IRaffleService {

    RaffleResponseDto createRaffle(RaffleRequestDto requestDto, Long userId);
}
