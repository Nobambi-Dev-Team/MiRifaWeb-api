package com.nobambidevteam.MiRifaWeb.modules.user.service;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IRoleRepository;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IRoleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoleService implements IRoleService {

    private final IRoleRepository roleRepository;


    @Override
    public RoleDto save(RoleCreateDto role) {

        Role newRole = Role.builder()
                .role(role.getRole())
                .build();

        return entityToDto(roleRepository.save(newRole));
    }

    private RoleDto entityToDto(Role role) {

        return RoleDto.builder()
                .id(role.getId())
                .role(role.getRole())
                .build();
    }

    @Override
    public Optional<Role> findByRole(String role) {
        return roleRepository.findByRole(role);
    }


    @Override
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        roleRepository.deleteById(id);
    }

    @Override
    public Role update(Role role) {
        return roleRepository.save(role);
    }


}
