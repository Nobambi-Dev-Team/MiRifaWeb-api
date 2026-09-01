package com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleNumbersStatusResponseDto;


public interface IRaffleService {

    RaffleResponseDto createRaffle(RaffleRequestDto requestDto, Long userId);

    RaffleNumbersStatusResponseDto getNumbersStatus(Long raffleId);
}
