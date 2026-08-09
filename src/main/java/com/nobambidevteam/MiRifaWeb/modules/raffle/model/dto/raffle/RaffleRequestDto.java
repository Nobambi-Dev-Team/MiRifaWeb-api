package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.Category;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RaffleRequestDto {

    @NotBlank(message = "El título es obligatorio")
    @Size(max = 150, message = "El título no puede superar los 150 caracteres")
    private String title;

    @Size(max = 500, message = "La descripción no puede superar los 500 caracteres")
    private String description;

    @NotNull(message = "La cantidad de números es obligatoria")
    @Min(value = 1, message = "Debe haber al menos 1 número en la rifa")
    @JsonProperty("number_count")
    private Integer numberCount;

    @NotNull(message = "El precio unitario es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio debe ser mayor a 0")
    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @NotBlank(message = "El alias o CBU es obligatorio")
    @JsonProperty("alias_cbu")
    private String aliasCbu;

    @Future(message = "La fecha de finalización debe ser en el futuro")
    @JsonProperty("end_date")
    private LocalDateTime endDate;

    @NotNull(message = "La categoría es obligatoria")
    private Category category;

    @JsonProperty("imagen_url")
    private String imageUrl;

    @NotEmpty(message = "Debe incluir al menos un premio")
    @Valid // Fundamental para que valide en cascada el PrizeRequestDTO
    private List<PrizeRequestDto> prizes;
}
