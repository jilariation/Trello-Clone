package ru.jilly.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Set;
import java.util.UUID;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class UserNotFoundException extends RuntimeException {
    private static final String USERNAME_PREFIX = "User wasn't found by username: %s";
    private static final String ID_PREFIX = "User wasn't found by id: %s";
    private static final String IDS_PREFIX = "User wasn't found by id in: %s";

    public UserNotFoundException(String username) {
        super(String.format(USERNAME_PREFIX, username));
    }
    public UserNotFoundException(Set<UUID> ids) {
        super(String.format(IDS_PREFIX, ids));
    }
    public UserNotFoundException(UUID id) {
        super(String.format(ID_PREFIX, id));
    }
}
