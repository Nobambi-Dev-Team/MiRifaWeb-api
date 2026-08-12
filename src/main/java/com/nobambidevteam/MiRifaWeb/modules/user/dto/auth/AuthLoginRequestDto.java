package com.nobambidevteam.MiRifaWeb.modules.user.dto.auth;

import jakarta.validation.constraints.NotBlank;

public record AuthLoginRequestDto(

    @NotBlank(message = "El username es obligatorio")
    String username,

    @NotBlank(message = "La contraseña es obligatoria")
    String password

){}
