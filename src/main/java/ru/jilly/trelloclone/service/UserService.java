package ru.jilly.trelloclone.service;

import ru.jilly.trelloclone.dto.user.UserRequest;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.User;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public interface UserService {
    List<UserResponse> getAllUsers();
    UserResponse getUserById(UUID id);
    UserResponse updateUserById(UUID id, UserRequest userDto, User currentUser);
    Set<UserResponse> getUsersByIdIn(Set<UUID> ids);
    List<UserResponse> getUsersByBoardId(UUID boardId);
}
