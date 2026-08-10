package com.nobambidevteam.MiRifaWeb.modules.user.repository;

import com.nobambidevteam.MiRifaWeb.modules.user.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface IRoleRepository extends JpaRepository<Role, Long> {

    Optional<Role> findByRole(String role);

}
