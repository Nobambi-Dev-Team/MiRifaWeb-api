package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle;

import java.math.BigDecimal;

public record RaffleMetricsDto(
        RaffleResponseDto raffle,
        int reservedNumbersCount,
        double completionPercentage,
        BigDecimal totalAmountRaised
){}
