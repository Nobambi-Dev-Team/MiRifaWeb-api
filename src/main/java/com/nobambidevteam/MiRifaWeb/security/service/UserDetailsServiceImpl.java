package com.nobambidevteam.MiRifaWeb.security.service;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.auth.AuthLoginRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.auth.AuthLoginResponseDto;
import com.nobambidevteam.MiRifaWeb.modules.user.entities.User;
import com.nobambidevteam.MiRifaWeb.modules.user.repository.IUserRepository;
import com.nobambidevteam.MiRifaWeb.utils.JwtUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final IUserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Obtenemos nuestro usuario SUerSec y devemos devolverlo en formato UserDetails
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("El usuario " + username + "no fue encontrado"));

        // Creamos una lista para los permisos
        List<SimpleGrantedAuthority> authorityList = new ArrayList<>();

        // traer roles y convertirlos en SimpleGrandAuthority
        user.getRoles()
                .forEach(role ->
                        authorityList.add(
                                new SimpleGrantedAuthority("ROLE_" + role.getRole())
                        )
                );

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.isEnabled(),
                user.isAccountNotExpired(),
                user.isCredentialNotExpired(),
                user.isAccountNotLocked(),
                authorityList // la lista con los roles en formato SimpleGrantedAuthority
        );

    }

    public AuthLoginResponseDto loginUser(AuthLoginRequestDto request) {

        // Recuperar nombre de usuario y contraseña
        String username = request.username();
        String password = request.password();

        Authentication authentication = this.authenticate(username, password);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.createToken(authentication);

        return new AuthLoginResponseDto(username, "Login successfull", accessToken, true);
    }

    private Authentication authenticate(String username, String password) {

        UserDetails userDetails = this.loadUserByUsername(username);

        if(!passwordEncoder.matches(password, userDetails.getPassword())){
            throw new BadCredentialsException("Invalid username or password");
        }

        return new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());
    }
}
