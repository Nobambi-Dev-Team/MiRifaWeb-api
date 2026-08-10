package com.nobambidevteam.MiRifaWeb.modules.user.service;


import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IUserRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IRoleService;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

    private final IUserRepository userRepository;
    private final IRoleService roleService;

    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public UserDto save(UserCreateDto user) {

        // Validaciones de negocio
        validateUniqueEmail(user.getEmail());

        // Asignar rol de organizador
        Role role = getOrganizerRole();
        Set<Role> roles = new HashSet<>();
        roles.add(role);

        User newUser = User.builder()
                .name(user.getName())
                .surname(user.getSurname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .password(encriptPassword(user.getPassword()))
                .createdAt(LocalDateTime.now())
                .roles(roles)
                .build();

        return entityToDto(userRepository.save(newUser));
    }

    private UserDto entityToDto(User entity) {

        return UserDto.builder()
                .id(entity.getId())
                .name(entity.getName())
                .surname(entity.getSurname())
                .email(entity.getEmail())
                .phoneNumber(entity.getPhoneNumber())
                .createdAt(entity.getCreatedAt())
                .roles(
                        entity.getRoles().stream()
                                .map(role -> new RoleDto(role.getId(), role.getRole()))
                                .collect(Collectors.toSet())
                )
                .build();
    }

    private Role getOrganizerRole() {

        String roleName = "ORGANIZER";
        return roleService.findByRole(roleName)
                .orElseThrow(() -> new EntityNotFoundException("No existe el rol " + roleName));
    }

    private void validateUniqueEmail(String email) {

        boolean existsUser = userRepository.findByEmail(email).isPresent();
        if (existsUser) {
            throw new IllegalArgumentException("Ya existe un usuario con el email " + email + " asociado.");
        }
    }

    @Override
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

//    @Override
//    public void update(User userSec) {
//        save(user);
//    }

    @Override
    public String encriptPassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}

