package com.nobambidevteam.MiRifaWeb.modules.user.service;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IUserRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IRoleService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private IRoleService roleService;

    @InjectMocks
    private UserService userService;

    @Captor
    private ArgumentCaptor<User> userCaptor;


    @Test
    public void create_ValidRequest_ReturnCreatedUser() {

        // Arrange
        String rawPassword = "12345678";
        UserCreateDto request = UserCreateDto.builder()
                .name("Cristian")
                .surname("Viscarra")
                .email("cristian@gmail.com")
                .phoneNumber("3855657564")
                .password(rawPassword)
                .build();

        String roleForUser = "ORGANIZER";

        Role role = Role.builder()
                .id(1L)
                .role(roleForUser)
                .build();

        User persistedUser = User.builder()
                .id(1L)
                .name("Cristian")
                .surname("Viscarra")
                .email("cristian@gmail.com")
                .phoneNumber("3855657564")
                .password(new BCryptPasswordEncoder().encode(rawPassword))
                .createdAt(LocalDateTime.now())
                .roles(Set.of(role))
                .build();


        when(userRepository.findByEmail(request.getEmail())).thenReturn(Optional.empty());
        when(roleService.findByRole(roleForUser)).thenReturn(Optional.of(role));
        when(userRepository.save(any(User.class))).thenReturn(persistedUser);

        // Act
        UserDto response = userService.save(request);

        // Asserts
        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals(request.getEmail(), response.getEmail());

        verify(userRepository).save(userCaptor.capture());
        User capturedUser = userCaptor.getValue();

        assertEquals(request.getName(), capturedUser.getName());
        assertEquals(request.getSurname(), capturedUser.getSurname());
        assertEquals(request.getEmail(), capturedUser.getEmail());
        assertEquals(request.getPhoneNumber(), capturedUser.getPhoneNumber());
        assertNotNull(capturedUser.getCreatedAt());
        assertTrue(capturedUser.getRoles().contains(role));

        // Verifica que la contraseña original no se guardó en texto plano
        assertNotEquals(rawPassword, capturedUser.getPassword());


    }


}
