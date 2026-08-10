package com.nobambidevteam.MiRifaWeb.modules.user.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateDto {

    @NotBlank(message = "El nombre es obligatorio")
    @Length(max = 50, message = "El nombre no debe contener más de 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Length(max = 50, message = "El apellido no debe contener más de 50 caracteres")
    private String surname;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @Length(max = 200, message = "El email no debe contener más de 200 caracteres")
    private String email;

    @NotBlank(message = "El número de contacto es obligatorio")
    @Pattern(regexp = "^[0-9]*$", message = "El número de teléfono solo debe contener números")
    @Length(max = 13, message = "El número de contacto no debe contener más de 13 caracteres")
    private String phoneNumber;

    @NotBlank(message = "La contraseña es obligatoria")
    @Length(max = 50, min = 8, message = "La contraseña debe contener al menos 8 caracteres")
    private String password;

//    private Set<Role> roles = new HashSet<>();
}
