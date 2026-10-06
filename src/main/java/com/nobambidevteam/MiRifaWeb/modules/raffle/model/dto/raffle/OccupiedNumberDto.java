package com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupiedNumberDto {
    private Integer number;
    private ReservationStatus status;
}