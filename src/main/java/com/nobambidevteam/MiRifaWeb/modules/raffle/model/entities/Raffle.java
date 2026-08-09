package com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.Category;
import jakarta.persistence.*;
import jdk.jfr.Name;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "raffles")
@AllArgsConstructor @NoArgsConstructor
@Getter @Setter
@Builder
public class Raffle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Name("raffle_id")
    private Long raffleId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false, length = 150)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(name = "number_count", nullable = false)
    private Integer numberCount;

    @Column(name = "unit_price", nullable = false, precision = 10, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "alias_cbu", nullable = false, length = 100)
    private String aliasCbu;

    @Column(name = "start_date", nullable = false, updatable = false)
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Category category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @OneToMany(mappedBy = "raffle", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Prize> prizes = new ArrayList<>();

    // Este método se ejecuta automáticamente justo antes de hacer el INSERT en la base de datos
    @PrePersist
    protected void onCreate() {
        this.startDate = LocalDateTime.now();
    }

    // Método utilitario (Helper) para sincronizar la relación bidireccional
    public void addPrize(Prize prize) {
        prizes.add(prize);
        prize.setRaffle(this);
    }
}
