package com.nobambidevteam.MiRifaWeb.modules.reservation.model.mapper;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;

public class ReservationMapper {

    private ReservationMapper() {
        throw new IllegalStateException("Clase de utilidad");
    }

    public static ReservationResponseDto toResponseDTO(Reservation reservation) {
        if (reservation == null) return null;

        return ReservationResponseDto.builder()
                .reservationId(reservation.getReservationId())
                .number(reservation.getNumber())
                .status(reservation.getStatus() != null ? reservation.getStatus().name() : null)
                .reservedAt(reservation.getReservedAt())
                .buyerName(reservation.getBuyerName())
                .buyerSurname(reservation.getBuyerSurname())
                .buyerEmail(reservation.getBuyerEmail())
                .buyerPhone(reservation.getBuyerPhone())
                .build();
    }

    public static Reservation toEntity(ReservationRequestDto dto) {
        if (dto == null) return null;

        Reservation reservation = new Reservation();
        reservation.setNumber(dto.getNumber());
        reservation.setBuyerName(dto.getBuyerName());
        reservation.setBuyerSurname(dto.getBuyerSurname());
        reservation.setBuyerEmail(dto.getBuyerEmail());
        reservation.setBuyerPhone(dto.getBuyerPhone());

        return reservation;
    }
}
