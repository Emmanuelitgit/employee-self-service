package employee_self_service.user_service.service;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.RoleSetup;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface RoleSetupService {

    ResponseEntity<ResponseDTO> saveRole(RoleSetup roleSetup);
    ResponseEntity<ResponseDTO> updateRole(RoleSetup roleSetup, UUID roleId);
    ResponseEntity<ResponseDTO> findRoleById(UUID roleId);
    ResponseEntity<ResponseDTO> deleteRole(UUID roleId);
    ResponseEntity<ResponseDTO> getRoles();
}
