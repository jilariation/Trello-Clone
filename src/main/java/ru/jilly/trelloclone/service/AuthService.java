package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.dto.auth.AuthResponse;
import ru.jilly.trelloclone.dto.auth.RegistrationRequest;

public interface AuthService {
    AuthResponse attemptLogin(String username, String password);
    void signUp(RegistrationRequest authRequest);
}
