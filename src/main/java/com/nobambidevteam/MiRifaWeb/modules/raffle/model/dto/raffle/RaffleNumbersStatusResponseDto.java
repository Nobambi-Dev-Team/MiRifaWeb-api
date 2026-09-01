package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleNumbersStatusResponseDto {

    @JsonProperty("total_capacity")
    private Integer totalCapacity;

    @JsonProperty("occupied_numbers")
    private List<OccupiedNumberDto> occupiedNumbers;
}
