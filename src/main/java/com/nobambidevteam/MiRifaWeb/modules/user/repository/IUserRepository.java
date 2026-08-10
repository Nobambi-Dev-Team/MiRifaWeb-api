package com.nobambidevteam.MiRifaWeb.modules.user.repository;

import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IUserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
