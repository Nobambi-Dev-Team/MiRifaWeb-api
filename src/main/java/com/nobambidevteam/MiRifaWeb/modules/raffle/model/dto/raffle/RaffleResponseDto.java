package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.Category;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RaffleResponseDto {

    private Long id;
    private String title;
    private String description;

    @JsonProperty("number_count")
    private Integer numberCount;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("alias_cbu")
    private String aliasCbu;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("end_date")
    private LocalDateTime endDate;

    private Category category;

    @JsonProperty("image_url")
    private String imageUrl;

    private List<PrizeResponseDto> prizes;
}