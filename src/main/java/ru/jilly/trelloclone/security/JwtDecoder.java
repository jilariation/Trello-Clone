package ru.jilly.trelloclone.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.jilly.trelloclone.security.exception.JwtAuthenticationException;

@Component
@RequiredArgsConstructor
public class JwtDecoder {
    private final JwtProperties properties;
    public DecodedJWT decode(String token) {
        try {
            return JWT.require(Algorithm.HMAC256(properties.getSecretKey()))
                    .build()
                    .verify(token);
        } catch (JWTVerificationException e) {
            throw new JwtAuthenticationException("JWT is expired or invalid");
        }
    }
}
