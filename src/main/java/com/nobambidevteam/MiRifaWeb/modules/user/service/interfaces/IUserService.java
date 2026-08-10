package com.nobambidevteam.MiRifaWeb.modules.user.service.interfaces;


import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserCreateDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.user.UserDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;

import java.util.List;
import java.util.Optional;

public interface IUserService {

    public List<User> findAll();

    public Optional<User> findById(Long id);

    public UserDto save(UserCreateDto user);

    public void deleteById(Long id);

//    public void update(User user);

    public String encriptPassword(String password);

}

