package com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReservationResponseDto {
    @JsonProperty("reservation_id")
    private Long reservationId;

    private Integer number;

    private String status;

    @JsonProperty("reserved_at")
    private LocalDateTime reservedAt;

    @JsonProperty("buyer_name")
    private String buyerName;

    @JsonProperty("buyer_surname")
    private String buyerSurname;

    @JsonProperty("buyer_email")
    private String buyerEmail;

    @JsonProperty("buyer_phone")
    private String buyerPhone;
}
