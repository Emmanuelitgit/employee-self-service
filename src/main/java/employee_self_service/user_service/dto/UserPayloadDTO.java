package employee_self_service.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.stereotype.Component;
import java.time.ZonedDateTime;
import java.util.UUID;

@Component
@Data
public class UserPayloadDTO {
    private UUID id;
    @NotBlank(message = "First name cannot be null or empty")
    @Size(message = "First name is too long or empty")
    private String firstName;
    @NotBlank(message = "Last name cannot be null or empty")
    @Size(message = "Last name is too long or empty")
    private String lastName;
    @NotBlank(message = "Email cannot be null or empty")
    @Email(message = "Invalid email or empty")
    private String email;
    @NotBlank(message = "Phone number cannot be null or empty")
    @Size(max = 10, min = 10)
    private String phone;
    @NotBlank(message = "Password cannot be null or empty")
    @Size(min = 4, max = 20, message = "Password must be between 8 and 20 characters")
//    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#&()–[{}]:;',?/*~$^+=<>]).*$",
//            message = "Password must contain at least one digit, one lowercase letter, one uppercase letter, and one special character")
    private String password;
    @NotBlank(message = "Username cannot be null or empty")
    private String username;
    @NotNull(message = "Role id cannot be null")
    private UUID role;
}
