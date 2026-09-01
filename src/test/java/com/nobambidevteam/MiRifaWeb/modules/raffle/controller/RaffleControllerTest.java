package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.reservation.model.dto.ReservationResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.reservation.service.ReservationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    @MockitoBean
    private ReservationService reservationService;

    private RaffleRequestDto validRequestDTO;
    private UsernamePasswordAuthenticationToken mockPrincipal;
    private final Long MOCK_USER_ID = 100L;

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

        mockPrincipal = new UsernamePasswordAuthenticationToken("usuario@email.com", null, List.of());
        mockPrincipal.setDetails(MOCK_USER_ID);
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
    void shouldReturn200AndPagedReservations() throws Exception {
        // Arrange
        Long raffleId = 1L;

        ReservationResponseDto mockReservation = ReservationResponseDto.builder()
                .reservationId(10L)
                .number(7)
                .status("RESERVED")
                .reservedAt(LocalDateTime.now())
                .buyerName("Luciano")
                .buyerSurname("Zanni")
                .buyerEmail("luciano@email.com")
                .buyerPhone("3851234567")
                .build();

        // Simulamos una página que devuelve nuestra reserva mockeada
        Page<ReservationResponseDto> mockPage = new PageImpl<>(
                List.of(mockReservation),
                PageRequest.of(0, 20),
                1 // Total de elementos
        );

        // Cuando el controlador llame al servicio con el ID de la rifa y el ID del usuario extraído del token
        when(reservationService.getReservationsByRaffle(eq(raffleId), eq(MOCK_USER_ID), any(Pageable.class)))
                .thenReturn(mockPage);

        // Act & Assert
        mockMvc.perform(get("/api/raffles/{raffle_id}/reservations", raffleId)
                        .principal(mockPrincipal) // Pasamos nuestra sesión falsa
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                // Verificamos la metadata de paginación
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.pageable.pageSize").value(20))
                // Verificamos datos de la reserva
                .andExpect(jsonPath("$.content[0].reservation_id").value(10))
                .andExpect(jsonPath("$.content[0].buyer_name").value("Luciano"))
                .andExpect(jsonPath("$.content[0].buyer_surname").value("Zanni"));
    }
}
