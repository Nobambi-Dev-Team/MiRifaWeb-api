package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import tools.jackson.databind.ObjectMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.interfaces.IRaffleService;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRaffleController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignoramos filtros de seguridad para probar la capa web aislada
class PublicRaffleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

    @MockitoBean
    private IRaffleService raffleService;

    private ReservationRequestDto validRequestDto;
    private final Long MOCK_RAFFLE_ID = 1L;

    @BeforeEach
    void setUp() {
        validRequestDto = new ReservationRequestDto(
                10,
                "Cristian",
                "Campos",
                "cristiank@gmail.com",
                "543855678798"
        );
    }

    // -------------------------------- CREAR RESERVA (feat-reservar-participante)

    @Test
    void shouldReturn201Created_WhenRequestIsValid() throws Exception {
        // Arrange
        doNothing().when(reservationService).createReservation(eq(MOCK_RAFFLE_ID), any(ReservationRequestDto.class));

        // Act & Assert
        mockMvc.perform(post("/api/public/raffles/{raffle_id}/reservations", MOCK_RAFFLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isCreated());

        // Verificamos que el controlador realmente le pasó los datos al servicio
        verify(reservationService).createReservation(eq(MOCK_RAFFLE_ID), any(ReservationRequestDto.class));
    }

    @Test
    void shouldReturn400BadRequest_WhenEmailIsInvalid() throws Exception {
        // Arrange
        validRequestDto.setBuyerEmail("correo-invalido"); // Rompemos el @Email del DTO

        // Act & Assert
        mockMvc.perform(post("/api/public/raffles/{raffle_id}/reservations", MOCK_RAFFLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn400BadRequest_WhenNumberIsNegative() throws Exception {
        // Arrange
        validRequestDto.setNumber(-5); // Rompemos el @Min(0) del DTO

        // Act & Assert
        mockMvc.perform(post("/api/public/raffles/{raffle_id}/reservations", MOCK_RAFFLE_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDto)))
                .andExpect(status().isBadRequest());
    }

    // -------------------------------- OBTENER DETALLES (develop-branch)

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