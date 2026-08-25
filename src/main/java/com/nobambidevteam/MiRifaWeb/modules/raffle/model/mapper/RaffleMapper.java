package com.nobambidevteam.MiRifaWeb.modules.raffle.model.mapper;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;

import java.util.stream.Collectors;

public class RaffleMapper {

    private RaffleMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    // RequestDTO a Entidad (Recibe el userId extraído del JWT por parámetro)
    public static Raffle toEntity(RaffleRequestDto dto, Long userId) {
        if (dto == null) return null;

        Raffle raffle = Raffle.builder()
                .userId(userId)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .numberCount(dto.getNumberCount())
                .unitPrice(dto.getUnitPrice())
                .aliasCbu(dto.getAliasCbu())
                .endDate(dto.getEndDate())
                .category(dto.getCategory())
                .imageUrl(dto.getImageUrl())
                // prizes se inicializa como una lista vacía por defecto
                .build();

        // Mapeamos la lista de premios y vinculamos la relación bidireccional
        if (dto.getPrizes() != null) {
            dto.getPrizes().forEach(prizeDto -> {
                raffle.addPrize(PrizeMapper.toEntity(prizeDto));
            });
        }

        return raffle;
    }

    // Entidad a ResponseDTO
    public static RaffleResponseDto toResponseDTO(Raffle raffle) {
        if (raffle == null) return null;

        return RaffleResponseDto.builder()
                .id(raffle.getRaffleId())
                .title(raffle.getTitle())
                .description(raffle.getDescription())
                .numberCount(raffle.getNumberCount())
                .unitPrice(raffle.getUnitPrice())
                .aliasCbu(raffle.getAliasCbu())
                .startDate(raffle.getStartDate())
                .endDate(raffle.getEndDate())
                .category(raffle.getCategory())
                .imageUrl(raffle.getImageUrl())
                // Mapeamos la lista de premios usando Streams
                .prizes(
                        raffle.getPrizes() != null
                                ? raffle.getPrizes().stream()
                                .map(PrizeMapper::toResponseDTO)
                                .collect(Collectors.toList())
                                : null
                )
                .build();
    }
}
