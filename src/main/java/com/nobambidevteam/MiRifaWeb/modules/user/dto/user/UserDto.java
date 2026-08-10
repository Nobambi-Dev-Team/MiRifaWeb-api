package com.nobambidevteam.MiRifaWeb.modules.user.dto.user;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.role.RoleDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private String phoneNumber;
    private LocalDateTime createdAt;

    @Builder.Default
    private Set<RoleDto> roles = new HashSet<>();

}
