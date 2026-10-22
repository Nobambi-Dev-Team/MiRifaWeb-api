package com.nobambidevteam.MiRifaWeb.modules.raffle.controller;

import com.nobambidevteam.MiRifaWeb.modules.raffle.model.entities.Raffle;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleCategory;
import com.nobambidevteam.MiRifaWeb.modules.raffle.model.enums.RaffleStatus;
import com.nobambidevteam.MiRifaWeb.modules.raffle.repository.RaffleRepository;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.entities.Reservation;
import com.nobambidevteam.MiRifaWeb.modules.reservation.model.enums.ReservationStatus;
import com.nobambidevteam.MiRifaWeb.modules.reservation.repository.ReservationRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IUserRepository;
import com.nobambidevteam.MiRifaWeb.security.principal.CustomUserPrincipal;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase
@Transactional
public class RaffleControllerIntTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private RaffleRepository raffleRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private IUserRepository userRepository;

    private static final String URL_BASE = "/api/raffles";


    // -------------------------------- GET METRICS (ORGANIZER)
    @Test
    public void getRaffleMetrics_ValidRequest_ReturnsOkAndExactMetrics() throws Exception {

        User user = User.builder()
                .name("Cristian")
                .surname("Campos")
                .email("test@test.com")
                .phoneNumber("385 5 362454")
                .password("1234")
                .build();
        user = userRepository.saveAndFlush(user);
        Long userId = user.getId();

        Raffle raffle = Raffle.builder()
                .userId(userId)
                .title("Rifa Solidaria H2")
                .description("Test de integración")
                .numberCount(100)
                .unitPrice(new BigDecimal("2000.00"))
                .aliasCbu("alias.mp")
                .startDate(LocalDateTime.now())
                .category(RaffleCategory.TRAVEL)
                .status(RaffleStatus.STARTED)
                .build();
        raffle = raffleRepository.saveAndFlush(raffle);
        Long raffleId = raffle.getRaffleId();

        Reservation res1 = createReservation(raffle, 5, ReservationStatus.RESERVED, "Ana", "Gomez");
        Reservation res2 = createReservation(raffle, 10, ReservationStatus.RESERVED, "Luis", "Silva");
        Reservation res3 = createReservation(raffle, 15, ReservationStatus.PENDING_PAYMENT, "Marta", "Ruiz");
        reservationRepository.saveAllAndFlush(List.of(res1, res2, res3));

        CustomUserPrincipal principal = new CustomUserPrincipal(userId, "test@test.com");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        // Act
        mvc.perform(
                        get(URL_BASE + "/{id}", raffleId)
                                .with(authentication(authToken))
                                .contentType(MediaType.APPLICATION_JSON))

                // Assert de la capa web y DTO
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.raffle.id").value(raffleId))
                .andExpect(jsonPath("$.raffle.title").value("Rifa Solidaria H2"))
                // Verificamos que el cálculo integral con la BD sea exacto
                .andExpect(jsonPath("$.reservedNumbersCount").value(3)) // Todas las reservas
                .andExpect(jsonPath("$.completionPercentage").value(2.0)) // 2 de 100 = 2%
                .andExpect(jsonPath("$.totalAmountRaised").value(4000.00)); // 2 pagadas * 2000
    }

    /**
     * Metodo utilitario para construir reservas
     */
    private Reservation createReservation(Raffle raffle, int number, ReservationStatus status, String name, String surname) {
        return Reservation.builder()
                .raffle(raffle)
                .number(number)
                .status(status)
                .buyerName(name)
                .buyerSurname(surname)
                .buyerEmail("test@test.com")
                .buyerPhone("3851234567")
                .build();
    }

    @Test
    public void getRaffleMetrics_UserWithoutOrganizerRole_ReturnsForbidden() throws Exception {
        // Arrange
        Long raffleId = 1L;

        CustomUserPrincipal principal = new CustomUserPrincipal(1L, "participante@test.com");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, new ArrayList<>() // Otorgamos una lista vacía para simular un usuario sin privilegios
        );

        // Act & Assert
        mvc.perform(
                        get(URL_BASE + "/{id}", raffleId)
                                .with(authentication(authToken))
                                .contentType(MediaType.APPLICATION_JSON))
                // Verificamos que Spring Security bloquee el acceso
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.title").value("Forbidden"));
        //.andExpect(jsonPath("$.detail").value("No tienes los permisos necesarios para realizar esta acción."));
    }

    @Test
    public void getRaffleMetrics_NonExistentRaffle_ReturnsNotFound() throws Exception {
        // Arrange
        Long idInexistente = 999L;

        CustomUserPrincipal principal = new CustomUserPrincipal(1L, "organizador@test.com");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        // Act & Assert
        mvc.perform(
                        get(URL_BASE + "/{id}", idInexistente)
                                .with(authentication(authToken))
                                .contentType(MediaType.APPLICATION_JSON))
                // Verificamos que la excepción ResourceNotFoundException devuelva un 404
                .andExpect(status().isNotFound());
        //.andExpect(jsonPath("$.message").value("No se encontró la rifa con id " + idInexistente));
    }

    @Test
    public void getRaffleMetrics_UserIsNotOwner_ReturnsConflict() throws Exception {
        // Arrange
        // Creamos al verdadero dueño
        User owner = User.builder()
                .name("Dueño")
                .surname("Real")
                .email("owner@test.com")
                .phoneNumber("1111111")
                .password("123")
                .build();
        owner = userRepository.saveAndFlush(owner);

        // Creamos a un usuario diferente (el que intentará acceder)
        User hacker = User.builder()
                .name("Hacker")
                .surname("Malo")
                .email("hacker@test.com")
                .phoneNumber("2222222")
                .password("123")
                .build();
        hacker = userRepository.saveAndFlush(hacker);

        // Persistimos la rifa a nombre del DUEÑO REAL
        Raffle raffle = Raffle.builder()
                .userId(owner.getId())
                .title("Rifa Privada")
                .numberCount(50)
                .unitPrice(new BigDecimal("1000.00"))
                .aliasCbu("alias.mp")
                .category(RaffleCategory.TRAVEL)
                .status(RaffleStatus.STARTED)
                .build();
        raffle = raffleRepository.saveAndFlush(raffle);

        // Inyectamos en el contexto de seguridad al usuario HACKER
        CustomUserPrincipal principal = new CustomUserPrincipal(hacker.getId(), "hacker@test.com");
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                principal, null, List.of(new SimpleGrantedAuthority("ROLE_ORGANIZER"))
        );

        // Act & Assert
        mvc.perform(
                get(URL_BASE + "/{id}", raffle.getRaffleId())
                        .with(authentication(authToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
                //.andExpect(jsonPath("$.message").value("No eres el propietario de esta rifa"));
    }
}
