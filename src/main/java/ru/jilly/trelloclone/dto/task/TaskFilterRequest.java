package ru.jilly.trelloclone.dto.task;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Min;
import lombok.*;
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
public class TaskFilterRequest {
    @Min(value = 0, message = "Page index should be greater than 0")
    private Integer pageIndex;
    @Min(value = 1, message = "Page size should be greater than 1")
    private Integer pageSize;
    private TaskFilterSort sort;
    private Set<String> tags;
    private Priority priority;
    private Status status;
    private Date endDate;
    private UUID creatorId;
    private UUID boardId;
}
