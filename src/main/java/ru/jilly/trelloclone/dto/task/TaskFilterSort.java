package ru.jilly.trelloclone.dto.task;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum TaskFilterSort {
    ID("id"),
    CREATOR_ID("creatorId"),
    CREATED_AT("createdAt"),
    CLOSED_AT("closedAt");
    private final String value;
}
