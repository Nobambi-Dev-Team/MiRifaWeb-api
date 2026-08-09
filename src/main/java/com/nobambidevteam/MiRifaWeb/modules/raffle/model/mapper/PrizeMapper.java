package com.nobambidevteam.MiRifaWeb.modules.raffle.model.mapper;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Prize;

public class PrizeMapper {

    private PrizeMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    public static Prize toEntity(PrizeRequestDto dto) {
        if (dto == null) return null;

        return Prize.builder()
                .position(dto.getPosition())
                .description(dto.getDescription())
                // El campo raffle no se setea aquí,
                // se encargará el método addPrize() en la entidad Raffle.
                .build();
    }

    public static PrizeResponseDto toResponseDTO(Prize prize) {
        if (prize == null) return null;

        return PrizeResponseDto.builder()
                .id(prize.getPrizeId())
                .position(prize.getPosition())
                .description(prize.getDescription())
                .build();
    }
}
