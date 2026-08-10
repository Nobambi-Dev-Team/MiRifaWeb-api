package com.nobambidevteam.MiRifaWeb.modules.user.controller;


import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final IUserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.findAll();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        Optional<User> user = userService.findById(id);
        return user.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto createUser(@RequestBody @Valid UserCreateDto user) {

        return userService.save(user);
    }
}

