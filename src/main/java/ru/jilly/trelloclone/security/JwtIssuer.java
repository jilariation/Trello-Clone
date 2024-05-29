package ru.jilly.trelloclone.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Configuration
@RequiredArgsConstructor
public class JwtIssuer {
    private final JwtProperties jwtProperties;

    public String issue(UserPrincipal userPrincipal) {
        return JWT.create()
                .withSubject(userPrincipal.getUser().getId().toString())
                .withExpiresAt(Instant.now().plus(Duration.of(1, ChronoUnit.DAYS)))
                .withClaim("username", userPrincipal.getUsername())
                .withClaim("authorities", userPrincipal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .sign(Algorithm.HMAC256(jwtProperties.getSecretKey()));
    }
}
