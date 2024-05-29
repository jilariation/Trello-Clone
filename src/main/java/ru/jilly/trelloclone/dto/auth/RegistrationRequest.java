package ru.jilly.trelloclone.dto.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegistrationRequest {
    @NotBlank(message = "Email can not be empty")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Username can not be empty")
    @Size(min = 4, max = 32, message = "Username size should be between 4 and 32")
    private String username;
    @Size(min = 6, max = 20, message = "Password size should be between 6 and 20")
    @NotBlank(message = "Password can not be empty")
    private String password;
}
