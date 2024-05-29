package ru.jilly.trelloclone.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.jilly.trelloclone.dto.user.UserRequest;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.User;
import ru.jilly.trelloclone.repo.UserRepo;
import ru.jilly.trelloclone.service.UserService;
import ru.jilly.trelloclone.utils.exception.EntityAlreadyExistException;
import ru.jilly.trelloclone.utils.exception.NotEnoughPermissionsException;
import ru.jilly.trelloclone.utils.exception.UserNotFoundException;
import ru.jilly.trelloclone.utils.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepo userRepo;
    private final UserMapper userMapper;

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepo.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserResponse getUserById(UUID id) {
        return userMapper.toDto(getExistingUser(id));
    }

    @Override
    @Transactional
    public UserResponse updateUserById(UUID id, UserRequest userDto, User currentUser) {
        User user = getExistingUser(id);

        validateOwnership(user, currentUser);

        try {
            userMapper.mergeDtoIntoEntity(userDto, user);
            User savedUser = userRepo.saveAndFlush(user);
            log.info("User with id: {} updated", savedUser.getId());
            return userMapper.toDto(savedUser);
        }
        catch (DataIntegrityViolationException e) {
            throw new EntityAlreadyExistException("User with this username or email already exists");
        }
    }

    @Override
    public Set<UserResponse> getUsersByIdIn(Set<UUID> ids) {
        Set<User> users = userRepo.findByIdIn(ids);
        if(users.isEmpty()) {
            throw new UserNotFoundException(ids);
        } else {
            return users.stream().map(userMapper::toDto).collect(Collectors.toSet());
        }
    }

    @Override
    public List<UserResponse> getUsersByBoardId(UUID boardId) {
        return userRepo.findMembersOfBoardByBoardId(boardId).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }


    private User getExistingUser(UUID id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    private void validateOwnership(User user, User currentUser) {
        if(!checkIfAllowedToModifyUser(user, currentUser)) {
            throw new NotEnoughPermissionsException(currentUser.getId(), user.getId());
        }
    }

    private boolean checkIfAllowedToModifyUser(User user, User currentUser) {
        return Objects.equals(user.getId(), currentUser.getId());
    }
}
