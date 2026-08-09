package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PrizeResponseDto {
    private Long id;
    private Integer position;
    private String description;
}
