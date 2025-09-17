package employee_self_service.user_service.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@Data
public class UpdateUserPayload {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private UUID role;
    private List<UUID> departments;
    private List<UUID> companies;
    private UUID managerId;
    private List<UUID> permissions;
}
