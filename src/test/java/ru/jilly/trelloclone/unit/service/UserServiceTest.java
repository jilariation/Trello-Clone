package ru.jilly.trelloclone.unit.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.jilly.trelloclone.utils.exception.UserNotFoundException;
import ru.jilly.trelloclone.dto.user.UserRequest;
import ru.jilly.trelloclone.service.impl.UserServiceImpl;
import ru.jilly.trelloclone.utils.exception.EntityAlreadyExistException;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class UserServiceTest {
    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = new User();
        user1.setId(UUID.randomUUID());
        user1.setUsername("user1");

        User user2 = new User();
        user2.setId(UUID.randomUUID());
        user2.setUsername("user2");

        UserResponse userResponse1 = new UserResponse();
        userResponse1.setId(user1.getId());
        userResponse1.setUsername("user1");

        UserResponse userResponse2 = new UserResponse();
        userResponse2.setId(user2.getId());
        userResponse2.setUsername("user2");

        when(userRepo.findAll()).thenReturn(List.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(userResponse1);
        when(userMapper.toDto(user2)).thenReturn(userResponse2);

        List<UserResponse> users = userService.getAllUsers();

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals("user1", users.get(0).getUsername());
        assertEquals("user2", users.get(1).getUsername());

        verify(userRepo, times(1)).findAll();
        verify(userMapper, times(1)).toDto(user1);
        verify(userMapper, times(1)).toDto(user2);
    }

    @Test
    void shouldGetUserById() {
        UUID userId = UUID.randomUUID();
        User user = new User();
        user.setId(userId);
        user.setUsername("user");

        UserResponse userResponse = new UserResponse();
        userResponse.setId(userId);
        userResponse.setUsername("user");

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(userResponse);

        UserResponse response = userService.getUserById(userId);

        assertNotNull(response);
        assertEquals(userId, response.getId());
        assertEquals("user", response.getUsername());

        verify(userRepo, times(1)).findById(userId);
        verify(userMapper, times(1)).toDto(user);
    }

    @Test
    void shouldThrowExceptionWhenGetUserByIdNotFound() {
        UUID userId = UUID.randomUUID();

        when(userRepo.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUserById(userId);
        });

        verify(userRepo, times(1)).findById(userId);
        verify(userMapper, times(0)).toDto(any(User.class));
    }

    @Test
    void shouldUpdateUserById() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        User currentUser = new User();
        currentUser.setId(userId);
        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).mergeDtoIntoEntity(any(UserRequest.class), any(User.class));
        when(userRepo.saveAndFlush(any(User.class))).thenReturn(user);
        when(userMapper.toDto(any(User.class))).thenReturn(new UserResponse());

        UserResponse response = userService.updateUserById(userId, userRequest, currentUser);

        assertNotNull(response);
        verify(userRepo, times(1)).findById(userId);
        verify(userMapper, times(1)).mergeDtoIntoEntity(any(UserRequest.class), any(User.class));
        verify(userRepo, times(1)).saveAndFlush(any(User.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatedUserIsNotCurrentUser() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        User currentUser = new User();
        currentUser.setId(UUID.randomUUID());
        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));

        NotEnoughPermissionsException thrown = assertThrows(
                NotEnoughPermissionsException.class,
                () -> userService.updateUserById(userId, userRequest, currentUser)
        );

        verify(userRepo, times(1)).findById(userId);
    }

    @Test
    void shouldThrowExceptionIfUserExistsWhenUpdateUser() {
        UUID userId = UUID.randomUUID();
        UserRequest userRequest = new UserRequest();
        User currentUser = new User();
        currentUser.setId(userId);
        User user = new User();
        user.setId(userId);

        when(userRepo.findById(userId)).thenReturn(Optional.of(user));
        doNothing().when(userMapper).mergeDtoIntoEntity(any(UserRequest.class), any(User.class));
        when(userRepo.saveAndFlush(any(User.class))).thenThrow(DataIntegrityViolationException.class);

        EntityAlreadyExistException thrown = assertThrows(
                EntityAlreadyExistException.class,
                () -> userService.updateUserById(userId, userRequest, currentUser)
        );

        assertEquals("User with this username or email already exists", thrown.getMessage());
        verify(userRepo, times(1)).findById(userId);
        verify(userMapper, times(1)).mergeDtoIntoEntity(any(UserRequest.class), any(User.class));
        verify(userRepo, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldGetUsersByIdInSuccess() {
        Set<UUID> userIds = Set.of(UUID.randomUUID(), UUID.randomUUID());
        User user1 = new User();
        user1.setId(userIds.iterator().next());
        User user2 = new User();
        user2.setId(userIds.iterator().next());

        when(userRepo.findByIdIn(userIds)).thenReturn(Set.of(user1, user2));
        when(userMapper.toDto(user1)).thenReturn(UserResponse.builder().id(user1.getId()).build());
        when(userMapper.toDto(user2)).thenReturn(UserResponse.builder().id(user2.getId()).build());

        Set<UserResponse> users = userService.getUsersByIdIn(userIds);

        assertNotNull(users);
        assertEquals(2, users.size());
        assertEquals(user1.getId(), users.iterator().next().getId());
        assertEquals(user2.getId(), users.iterator().next().getId());
        verify(userRepo, times(1)).findByIdIn(userIds);
    }

    @Test
    void shouldThrowExceptionWhenGetUsersByIdsInIsEmpty() {
        Set<UUID> userIds = Set.of(UUID.randomUUID(), UUID.randomUUID());

        when(userRepo.findByIdIn(userIds)).thenReturn(Set.of());

        assertThrows(UserNotFoundException.class, () -> {
            userService.getUsersByIdIn(userIds);
        });

        verify(userRepo, times(1)).findByIdIn(userIds);
    }
}
