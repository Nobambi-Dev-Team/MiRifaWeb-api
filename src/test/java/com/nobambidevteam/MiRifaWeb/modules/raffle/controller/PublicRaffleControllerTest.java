package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRaffleController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignoramos filtros de seguridad para probar la capa web aislada
class PublicRaffleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRaffleService raffleService;

    @Test
    void shouldReturn200AndRaffleDetailsWhenRaffleExists() throws Exception {
        // Arrange
        Long raffleId = 1L;

        PrizeResponseDto prize1 = PrizeResponseDto.builder()
                .id(1L)
                .position(1)
                .description("Canasta familiar grande")
                .build();

        RaffleResponseDto mockResponse = RaffleResponseDto.builder()
                .id(raffleId)
                .title("Sorteo de fin de año")
                .description("Recaudación para el viaje de estudios")
                .numberCount(100)
                .unitPrice(new BigDecimal("1500.50"))
                .aliasCbu("mi.alias.mp")
                .startDate(LocalDateTime.parse("2026-11-10T18:30:00"))
                .endDate(LocalDateTime.parse("2026-12-20T23:59:59"))
                .category(RaffleCategory.TRAVEL)
                .prizes(List.of(prize1))
                .build();

        // Le indicamos a Mockito qué debe devolver cuando se llame al servicio
        when(raffleService.getPublicRaffleDetails(raffleId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/public/raffles/{raffle_id}", raffleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sorteo de fin de año"))
                .andExpect(jsonPath("$.description").value("Recaudación para el viaje de estudios"))
                .andExpect(jsonPath("$.number_count").value(100))
                .andExpect(jsonPath("$.unit_price").value(1500.50))
                .andExpect(jsonPath("$.category").value("TRAVEL"))
                .andExpect(jsonPath("$.prizes").isArray())
                .andExpect(jsonPath("$.prizes[0].position").value(1))
                .andExpect(jsonPath("$.prizes[0].description").value("Canasta familiar grande"));
    }
}