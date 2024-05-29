package ru.jilly.trelloclone.utils.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ControllerErrorResponse {
    private String message;
    private long timestamp;
}
