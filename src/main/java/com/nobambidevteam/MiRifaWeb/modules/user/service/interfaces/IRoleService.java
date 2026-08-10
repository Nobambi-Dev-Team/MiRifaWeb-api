package com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    List<Role> findAll();

    Optional<Role> findById(Long id);

    Optional<Role> findByRole(String role);

    RoleDto save(RoleCreateDto role);

    void deleteById(Long id);

    Role update(Role role);

}
