package ru.jilly.trelloclone.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


@Component
public class AuthenticationFacade implements IAuthenticationFacade{
    @Override
    public UserPrincipal getAuthenticationPrincipal() {
        return (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
