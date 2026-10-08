package com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {

    @NotNull(message = "El número es obligatorio")
    @Min(value = 0, message = "El número no puede ser negativo")
    private Integer number;

    @NotBlank(message = "El nombre es obligatorio")
    @JsonProperty("buyer_name")
    private String buyerName;

    @NotBlank(message = "El apellido es obligatorio")
    @JsonProperty("buyer_surname")
    private String buyerSurname;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Debe ser un correo válido")
    @JsonProperty("buyer_email")
    private String buyerEmail;

    @NotBlank(message = "El teléfono es obligatorio")
    @JsonProperty("buyer_phone")
    private String buyerPhone;
}
