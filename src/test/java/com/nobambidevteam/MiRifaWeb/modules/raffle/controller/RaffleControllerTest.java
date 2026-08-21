package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.prize.PrizeRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.dto.raffle.RaffleResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.Category;
import com.nobambidevteam.MiRifaWeb.modules.raffle.service.RaffleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
                Category.VIAJES,
                "url",
                List.of(prize)
        );

        // Simulamos la Opción A o B que hablamos anteriormente (un usuario con ID 1)
        // Spring MVC pasará este principal al parámetro Authentication del controller
        mockPrincipal = new UsernamePasswordAuthenticationToken("1", null, List.of());
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
}
