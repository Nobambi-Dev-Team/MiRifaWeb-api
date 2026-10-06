package com.nobambidevteam.MiRifaWeb.modules.reservation.repository;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.OccupiedNumberDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    Page<Reservation> findByRaffleId(Long raffleId, Pageable pageable);

    @Query("SELECT new com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.OccupiedNumberDto(r.number, r.status) " +
            "FROM Reservation r WHERE r.raffle.raffleId = :raffleId")
    List<OccupiedNumberDto> findOccupiedNumbersByRaffleId(@Param("raffleId") Long raffleId);
}
