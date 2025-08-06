package hexlet.code.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserCreateDTO {

    private String firstName;
    private String lastName;

    @Email(message = "Некорректный формат email")
    @NotBlank(message = "Email не должен быть пустым")
    private String email;

    @Size(min = 3, max = 100, message = "Пароль должен содержать минимум 3 символа")
    @NotBlank(message = "Пароль не должен быть пустым")
    private String password;
}
