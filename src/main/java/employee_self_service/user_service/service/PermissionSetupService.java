package employee_self_service.user_service.service;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.dto.UserPayloadDTO;
import employee_self_service.user_service.models.PermissionSetup;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PermissionSetupService {
    ResponseEntity<ResponseDTO> createPermission(PermissionSetup permissionSetup);
    ResponseEntity<ResponseDTO> getPermissions();
    ResponseEntity<ResponseDTO> getPermissionById(UUID permissionId);
    ResponseEntity<ResponseDTO> updatePermission(PermissionSetup permissionSetup, UUID permissionId);
    ResponseEntity<ResponseDTO> removePermission(UUID permissionId);
}
