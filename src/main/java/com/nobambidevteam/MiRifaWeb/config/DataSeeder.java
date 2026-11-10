package com.nobambidevteam.MiRifaWeb.config;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IRoleRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IUserRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {
    private final IUserService userService;
    private final IUserRepository userRepository;
    private final IRoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Crear el rol si no existe
        if (roleRepository.findByRole("ORGANIZER").isEmpty()) {
            Role role = new Role();
            role.setRole("ORGANIZER");
            roleRepository.save(role);
        }

        // Crear el usuario si no existe
        if (userRepository.findByEmail("luciano@email.com").isEmpty()) {
            UserCreateDto testUser = new UserCreateDto();
            testUser.setName("Luciano");
            testUser.setSurname("Zanni");
            testUser.setEmail("luciano@email.com");
            testUser.setPhoneNumber("3851234567");
            testUser.setPassword("123456");

            // Usamos el servicio que ya tiene la lógica de encriptación y asignación de roles
            userService.save(testUser);
            System.out.println("Usuario de prueba creado con éxito.");
        }
    }
}
