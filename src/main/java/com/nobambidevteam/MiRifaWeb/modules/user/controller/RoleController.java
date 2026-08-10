package com.nobambidevteam.MiRifaWeb.modules.user.controller;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class RoleController {

    private final IRoleService roleService;


    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.findAll();
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getRoleById(@PathVariable Long id) {
        Optional<Role> role = roleService.findById(id);
        return role.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<RoleDto> createRole(@RequestBody @Valid RoleCreateDto role) {
        return ResponseEntity.ok(roleService.save(role));
    }


}
