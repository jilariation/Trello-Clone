package ru.jilly.trelloclone.dto.checklist;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import ru.jilly.trelloclone.dto.checkitem.CheckItemRequest;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddChecklistRequest {
    @NotBlank(message = "Checklist name can't be empty")
    private String name;
    private Set<CheckItemRequest> checkItems;
}
