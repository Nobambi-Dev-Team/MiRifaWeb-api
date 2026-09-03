package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.security.principal.CustomUserPrincipal;
import tools.jackson.databind.ObjectMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.RaffleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.OccupiedNumberDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleNumbersStatusResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

@WebMvcTest(RaffleController.class)
@AutoConfigureMockMvc(addFilters = false) // Ignoramos filtros JWT para esta prueba unitaria
class RaffleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private RaffleService raffleService;

    private RaffleRequestDto validRequestDTO;
    private UsernamePasswordAuthenticationToken mockPrincipal;

    @BeforeEach
    void setUp() {
        PrizeRequestDto prize = new PrizeRequestDto(1, "Canasta familiar");
        validRequestDTO = new RaffleRequestDto(
                "Sorteo de fin de año",
                "Descripción",
                100,
                new BigDecimal("1500.50"),
                "mi.alias.mp",
                LocalDateTime.now().plusMonths(1),
                RaffleCategory.TRAVEL,
                "url",
                List.of(prize)
        );

        // Creamos la instancia de nuestro principal personalizado con el ID esperado por Mockito (1L)
        CustomUserPrincipal customPrincipal = new CustomUserPrincipal(1L, "usuario@test.com");

        // Pasamos el customPrincipal como primer argumento
        mockPrincipal = new UsernamePasswordAuthenticationToken(customPrincipal, null, List.of());
    }

    @Test
    void shouldReturn201WhenValidRequest() throws Exception {
        RaffleResponseDto mockResponse = RaffleResponseDto.builder()
                .id(1L)
                .title("Sorteo de fin de año")
                .build();

        // Le decimos a Mockito: Cuando el service reciba cualquier RequestDTO y el ID 1L, devolvé el mockResponse
        when(raffleService.createRaffle(any(RaffleRequestDto.class), eq(1L))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/raffles")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Sorteo de fin de año"));
    }

    @Test
    void shouldReturn400WhenTitleIsMissing() throws Exception {
        // Modificamos el DTO para que sea inválido (título nulo)
        validRequestDTO.setTitle(null);

        mockMvc.perform(post("/api/raffles")
                        .principal(mockPrincipal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequestDTO)))
                // Spring Boot Validation debe interceptar la falla y devolver 400 Bad Request
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn200AndNumbersStatusWhenRaffleExists() throws Exception {
        // Arrange
        Long raffleId = 1L;

        List<OccupiedNumberDto> mockOccupiedNumbers = List.of(
                new OccupiedNumberDto(7, ReservationStatus.RESERVED),
                new OccupiedNumberDto(10, ReservationStatus.RESERVED)
        );

        RaffleNumbersStatusResponseDto mockResponse = RaffleNumbersStatusResponseDto.builder()
                .totalCapacity(100)
                .occupiedNumbers(mockOccupiedNumbers)
                .build();

        // Le decimos a Mockito qué devolver cuando se llame a la función
        when(raffleService.getNumbersStatus(raffleId)).thenReturn(mockResponse);

        // Act & Assert
        mockMvc.perform(get("/api/raffles/{raffle_id}/numbers-status", raffleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Validamos la estructura JSON resultante usando JsonPath
                .andExpect(jsonPath("$.total_capacity").value(100))
                .andExpect(jsonPath("$.occupied_numbers").isArray())
                .andExpect(jsonPath("$.occupied_numbers.length()").value(2))
                .andExpect(jsonPath("$.occupied_numbers[0].number").value(7))
                .andExpect(jsonPath("$.occupied_numbers[0].status").value("RESERVED"))
                .andExpect(jsonPath("$.occupied_numbers[1].number").value(10))
                .andExpect(jsonPath("$.occupied_numbers[1].status").value("RESERVED"));
    }
}
