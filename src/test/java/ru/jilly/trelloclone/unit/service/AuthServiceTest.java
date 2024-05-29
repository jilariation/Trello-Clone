package ru.jilly.trelloclone.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.entity.enums.Role;
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.security.JwtIssuer;
import ru.jilly.trelloclone.security.UserPrincipal;
import ru.jilly.trelloclone.service.impl.AuthServiceImpl;
import ru.jilly.trelloclone.utils.exception.EntityAlreadyExistException;
import ru.jilly.trelloclone.dto.auth.AuthResponse;
import ru.jilly.trelloclone.dto.auth.RegistrationRequest;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class AuthServiceTest {
    @Mock
    private JwtIssuer jwtIssuer;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserRepo userRepo;
    @Mock
    private PasswordEncoder bCryptPasswordEncoder;
    @Mock
    private UserMapper userMapper;
    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldLoginUser() {
        String username = "testuser";
        String password = "testpass";
        User user = new User();
        user.setUsername(username);
        UserPrincipal principal = UserPrincipal.builder().user(user).build();
        Authentication auth = new UsernamePasswordAuthenticationToken(principal, password);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(auth);
        when(jwtIssuer.issue(any(UserPrincipal.class))).thenReturn("testtoken");
        when(userMapper.toDto(any(User.class))).thenReturn(UserResponse.builder().username(username).build());

        AuthResponse response = authService.attemptLogin(username, password);

        assertNotNull(response);
        assertEquals("testtoken", response.getToken());
        verify(authenticationManager, times(1)).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtIssuer, times(1)).issue(any(UserPrincipal.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void shouldSignUpUser() {
        RegistrationRequest request = new RegistrationRequest("testuser@example.com",
                "testuser", "testpass");
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("testpass");
        user.setRole(Role.ROLE_USER);

        when(userMapper.toEntity(any(RegistrationRequest.class))).thenReturn(user);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedpass");
        when(userRepo.saveAndFlush(any(User.class))).thenReturn(user);

        authService.signUp(request);

        verify(userMapper, times(1)).toEntity(any(RegistrationRequest.class));
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepo, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserExist() {
        RegistrationRequest request = new RegistrationRequest("testuser@example.com", "testuser", "testpass");
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("testuser@example.com");
        user.setPassword("testpass");
        user.setRole(Role.ROLE_USER);

        when(userMapper.toEntity(any(RegistrationRequest.class))).thenReturn(user);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn("encodedpass");
        when(userRepo.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        EntityAlreadyExistException thrown = assertThrows(
                EntityAlreadyExistException.class,
                () -> authService.signUp(request)
        );

        assertEquals("User with this username or email already exists", thrown.getMessage());
        verify(userMapper, times(1)).toEntity(any(RegistrationRequest.class));
        verify(bCryptPasswordEncoder, times(1)).encode(anyString());
        verify(userRepo, times(1)).saveAndFlush(any(User.class));
    }
}
