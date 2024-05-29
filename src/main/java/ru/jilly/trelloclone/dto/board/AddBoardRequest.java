package ru.jilly.trelloclone.dto.board;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.jilly.trelloclone.utils.validation.UpdateValidation;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AddBoardRequest {
    @Size(max = 256, message = "Board name length can't be greater than 256", groups = {UpdateValidation.class})
    @NotBlank(message = "Board name can't be null")
    private String name;
    @Size(max = 1024, message = "Board description length can't be greater than 1024", groups = {UpdateValidation.class})
    @NotBlank(message = "Board name can't be null")
    private String description;
    private Set<UUID> memberIds;
}
