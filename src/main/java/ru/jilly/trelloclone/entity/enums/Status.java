package ru.jilly.trelloclone.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Status {
    TODO("To do"),
    DOING("Doing"),
    DONE("Done"),
    ON_REVIEW("On review");
    private final String value;
}
