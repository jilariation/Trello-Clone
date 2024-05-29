package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.entity.enums.Role;
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.security.JwtIssuer;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.dto.auth.AuthResponse;
import ru.jilly.trelloclone.dto.auth.RegistrationRequest;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.service.AuthService;
import ru.jilly.trelloclone.utils.exception.EntityAlreadyExistException;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final JwtIssuer jwtIssuer;
    private final AuthenticationManager authenticationManager;
    private final UserRepo userRepo;
    private final PasswordEncoder bCryptPasswordEncoder;
    private final UserMapper userMapper;
    private final KafkaMessagingServiceImpl kafkaMessagingService;

    @Override
    public AuthResponse attemptLogin(String username, String password) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        String token = jwtIssuer.issue(principal);
        return AuthResponse.builder()
                .token(token)
                .user(userMapper.toDto(principal.getUser()))
                .build();
    }

    @Transactional
    @Override
    public void signUp(RegistrationRequest authRequest) {
        User user = userMapper.toEntity(authRequest);
        user.setRole(Role.ROLE_USER);
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        try {
            User savedUser = userRepo.saveAndFlush(user);
            log.info("User with username : {} registered", savedUser.getUsername());
            kafkaMessagingService.sendSignUpUser(savedUser);
        } catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistException("User with this username or email already exists");
        }
    }
}
