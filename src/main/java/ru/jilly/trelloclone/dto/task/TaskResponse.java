package ru.jilly.trelloclone.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import ru.jilly.trelloclone.dto.user.UserResponse;
import ru.jilly.trelloclone.entity.enums.Priority;
import ru.jilly.trelloclone.entity.enums.Status;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class TaskResponse {
    private UUID id;
    private String name;
    private String description;
    private Priority priority;
    private Status status;
    private UserResponse creator;
    private Set<UserResponse> executors;
    private Set<String> tags;
    private Long commentsCount;
    private Long attachmentsCount;
    private Long checkItemsCount;
    private Long checkItemsCheckedCount;
    private Date endDate;
    private Date createdAt;
    private Date updatedAt;
    private Date closedAt;
}
