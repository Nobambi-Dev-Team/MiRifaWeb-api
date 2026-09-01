package com.nobambidevteam.MiRifaWeb.modules.reservation.repository;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Page<Reservation> findByRaffleId(Long raffleId, Pageable pageable);
}
