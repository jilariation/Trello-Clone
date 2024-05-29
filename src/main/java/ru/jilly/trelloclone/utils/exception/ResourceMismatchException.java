package ru.jilly.trelloclone.utils.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ResourceMismatchException extends RuntimeException {
    public ResourceMismatchException(String message) {
        super(message);
    }
}
