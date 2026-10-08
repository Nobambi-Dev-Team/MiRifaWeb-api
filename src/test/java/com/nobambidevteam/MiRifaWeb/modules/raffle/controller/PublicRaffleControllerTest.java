package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.service.RaffleService;
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
import tools.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicRaffleController.class)
@AutoConfigureMockMvc(addFilters = false)
class PublicRaffleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReservationService reservationService;

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
}
