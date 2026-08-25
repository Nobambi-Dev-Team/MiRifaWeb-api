package com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "prizes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Prize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "prize_id")
    private Long prizeId;

    @Column(nullable = false)
    private Integer position;

    @Column(nullable = false, length = 255)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raffle_id", nullable = false)
    private Raffle raffle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "winning_reservation_id")
    private Reservation winningReservation;
}
