package com.nobambidevteam.MiRifaWeb.modules.user.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Column(name = "surname", length = 50, nullable = false)
    private String surname;

    @Column(name = "email", length = 200, nullable = false, unique = true)
    private String email;

    @Column(name = "phone_number", length = 13, nullable = false)
    private String phoneNumber;

    @Column(name = "password", length = 250, nullable = false)
    private String password;

    private LocalDateTime createdAt;

    @Builder.Default
    @Column(nullable = false)
    private boolean enabled = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean accountNotExpired = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean accountNotLocked = true;

    @Builder.Default
    @Column(nullable = false)
    private boolean credentialNotExpired = true;


    @ManyToMany(fetch = FetchType.EAGER) //Eager carga todos los roles
    @JoinTable(
            name="user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name="role_id")
    )
    @Builder.Default
    private Set<Role> roles = new HashSet<>();


}
