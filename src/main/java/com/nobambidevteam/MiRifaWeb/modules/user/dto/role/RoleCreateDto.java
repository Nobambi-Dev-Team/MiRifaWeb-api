package com.nobambidevteam.MiRifaWeb.modules.user.dto.role;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoleCreateDto {

    @NotBlank(message = "El rol debe contener un nombre valido")
    private String role;
}
