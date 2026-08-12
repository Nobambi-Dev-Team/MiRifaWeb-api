package com.nobambidevteam.MiRifaWeb.modules.user.controller;

import com.nobambidevteam.MiRifaWeb.modules.user.dto.auth.AuthLoginRequestDto;
import com.nobambidevteam.MiRifaWeb.modules.user.dto.auth.AuthLoginResponseDto;
import com.nobambidevteam.MiRifaWeb.security.service.UserDetailsServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final UserDetailsServiceImpl userDetailsService;


    @PostMapping("/login")
    public ResponseEntity<AuthLoginResponseDto> login(@RequestBody @Valid AuthLoginRequestDto request){

        return new ResponseEntity<>(this.userDetailsService.loginUser(request), HttpStatus.OK);

    }

    @GetMapping("/verify-sec")
    @PreAuthorize("hasRole('ORGANIZER')")
    public String verifySecurityForToken(){
        return "Tu token es valido";
    }

}
