package ru.jilly.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;
@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    private static final String ID_PREFIX = "Resource wasn't found by id: %s";

    public ResourceNotFoundException(UUID id) {
        super(String.format(ID_PREFIX, id));
    }
}
