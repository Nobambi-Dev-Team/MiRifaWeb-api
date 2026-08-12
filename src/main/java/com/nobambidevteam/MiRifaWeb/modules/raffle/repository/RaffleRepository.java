package com.nobambidevteam.MiRifaWeb.modules.raffle.repository;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RaffleRepository extends JpaRepository<Raffle, Long> {
}
