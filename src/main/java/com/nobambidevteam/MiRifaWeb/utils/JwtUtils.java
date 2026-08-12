package com.nobambidevteam.MiRifaWeb.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.private.key}")
    private String privateKey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;

    // Creación de Tokens
    public String createToken(Authentication authentication) {

        Algorithm algorithm = Algorithm.HMAC256(privateKey);

        String username = authentication.getPrincipal().toString();

        String authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        return JWT.create()
                .withIssuer(this.userGenerator) // userGenerator = encargado de generar el Token
                .withSubject(username) // El usuario que se autenticó y va a estar en los datos encriptados
                .withClaim("authorities", authorities)
                .withIssuedAt(new Date())
                .withExpiresAt(
                        new Date(
                                System.currentTimeMillis() +
                                        TimeUnit.DAYS.toMillis(3) // El token será valido por tres dias
                        )
                )
                .withJWTId(UUID.randomUUID().toString())
                .withNotBefore(new Date(System.currentTimeMillis()))
                .sign(algorithm); // Firma
    }

    // Decodificar y validar Tokens
    public DecodedJWT validateToken(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(privateKey);
            JWTVerifier jwtVerifier = JWT
                    .require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

            return jwtVerifier.verify(token);

        } catch (JWTVerificationException e) {
            throw new JWTVerificationException("Invalid Token. Not authorized. " +
                    "\nDetails" + e.getMessage());
        }
    }

    // Obtener el usuario/username del token
    public String extractUsername (DecodedJWT decodedJWT) {
        return decodedJWT.getSubject().toString();
    }

    // Obtener todos los claims
    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }

    // Obtener un claim especifico
    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }
}
