package ru.jilly.trelloclone.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthenticationFacade {
    UserDetails getAuthenticationPrincipal();
}
