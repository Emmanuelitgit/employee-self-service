package employee_self_service.user_service.service;

import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserRoleService {
    ResponseEntity<ResponseDTO> saveUserRole(UUID userId, UUID roleId);
}
