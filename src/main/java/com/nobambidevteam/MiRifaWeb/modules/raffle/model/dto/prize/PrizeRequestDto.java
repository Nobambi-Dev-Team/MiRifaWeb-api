package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PrizeRequestDto {

    @NotNull(message = "La posición del premio es obligatoria")
    @Min(value = 1, message = "La posición debe ser mayor a 0")
    private Integer position;

    @NotBlank(message = "La descripción del premio no puede estar vacía")
    private String description;
}
