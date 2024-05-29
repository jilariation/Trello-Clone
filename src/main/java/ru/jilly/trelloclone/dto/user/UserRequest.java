package ru.jilly.trelloclone.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;
import ru.jilly.trelloclone.utils.validation.UpdateValidation;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserRequest {
    @Size(min = 2, max = 32, message = "Name size should be between 2 and 32", groups = {UpdateValidation.class})
    private String name;
    @Size(min = 2, max = 32, message = "Lastname size should be between 2 and 32", groups = {UpdateValidation.class})
    private String lastName;
    @Email(message = "Email should be valid", groups = {UpdateValidation.class})
    private String email;
}
