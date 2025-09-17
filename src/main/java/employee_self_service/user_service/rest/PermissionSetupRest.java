package employee_self_service.user_service.rest;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.PermissionSetup;
import employee_self_service.user_service.serviceImpl.PermissionSetupServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Permission Management")
@RestController
@RequestMapping("/api/v1/permissions")
public class PermissionSetupRest {
    private final PermissionSetupServiceImpl permissionSetupService;

    @Autowired
    public PermissionSetupRest(PermissionSetupServiceImpl permissionSetupService) {
        this.permissionSetupService = permissionSetupService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createPermission(@RequestBody PermissionSetup permissionSetup){
        return permissionSetupService.createPermission(permissionSetup);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getPermissions(){
        return permissionSetupService.getPermissions();
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<ResponseDTO> getPermissionById(@PathVariable UUID permissionId){
        return permissionSetupService.getPermissionById(permissionId);
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<ResponseDTO> updatePermission(@RequestBody PermissionSetup permissionSetup,
                                                        @PathVariable UUID permissionId){
        return permissionSetupService.updatePermission(permissionSetup, permissionId);
    }
}
