package ru.jilly.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class NotEnoughPermissionsException extends RuntimeException {
    private static final String MESSAGE = "User with id: %s is not allowed to modify resource with id: %s";
    public NotEnoughPermissionsException(UUID userId, UUID resourceId) {
        super(String.format(MESSAGE, userId, resourceId));
    }
}
