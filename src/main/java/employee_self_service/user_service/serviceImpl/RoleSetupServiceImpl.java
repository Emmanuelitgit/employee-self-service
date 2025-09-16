package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.external.KeycloakService;
import employee_self_service.user_service.models.RoleSetup;
import employee_self_service.user_service.repo.RoleSetupRepo;
import employee_self_service.user_service.service.RoleSetupService;
import employee_self_service.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class RoleSetupServiceImpl implements RoleSetupService {

    private final RoleSetupRepo roleSetupRepo;
    private final KeycloakService keycloakService;

    @Autowired
    public RoleSetupServiceImpl(RoleSetupRepo roleSetupRepo, KeycloakService keycloakService) {
        this.roleSetupRepo = roleSetupRepo;
        this.keycloakService = keycloakService;
    }

    /**
     * @description This method is used to save role setup record.
     * @param roleSetup
     * @return ResponseEntity containing the saved role record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> saveRole(RoleSetup roleSetup) {
      try {
          /**
           * validating payload
           */
          if (roleSetup == null){
              ResponseDTO  response = AppUtils.getResponseDto("payload cannot be null", HttpStatus.BAD_REQUEST);
              return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
          }
          /**
           * saving record to db and keycloak
           */
          RoleSetup roleSetupRes = roleSetupRepo.save(roleSetup);
          keycloakService.saveRoleToKeycloak(roleSetup);

          /**
           * return response on success
           */
          ResponseDTO  response = AppUtils.getResponseDto("role record added successfully", HttpStatus.CREATED, roleSetupRes);
          return new ResponseEntity<>(response, HttpStatus.CREATED);

      }catch (Exception e) {
          log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
          ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    /**
     * @description This method is used to update role setup record.
     * @param roleSetup
     * @return ResponseEntity containing the updated role record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateRole(RoleSetup roleSetup, UUID roleId) {
        try {
            /**
             * check if role exist
             */
            RoleSetup existingRoleSetup = roleSetupRepo.findById(roleId)
                    .orElseThrow(()-> new NotFoundException("role setup record not found"));

            /**
             * saving record
             */
            existingRoleSetup.setName(roleSetup.getName() != null? roleSetup.getName():existingRoleSetup.getName());
            keycloakService.updateRole(existingRoleSetup.getName(), roleSetup.getName());
            /**
             * return response on success
             */
            ResponseDTO  response = AppUtils.getResponseDto("role records updated successfully", HttpStatus.OK, roleSetup);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    /**
     * @description This method is used to fetch ole setup records given the id.
     * @param roleId
     * @return ResponseEntity containing the retrieved role record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> findRoleById(UUID roleId) {
        try {
            /**
             * check if record exist
             */
            Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
            if (roleSetupOptional.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("role record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            RoleSetup roleSetup = roleSetupOptional.get();
            ResponseDTO  response = AppUtils.getResponseDto("role records fetched successfully", HttpStatus.OK, roleSetup);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to delete role setup record.
     * @param roleId
     * @return ResponseEntity containing message and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> deleteRole(UUID roleId) {
       try {
           /**
            * check if record exist
            */
           RoleSetup roleSetup = roleSetupRepo.findById(roleId)
                   .orElseThrow(()-> new NotFoundException("role setup record not found"));
           /**
            * delete record from db and keycloak
            */
           roleSetupRepo.deleteById(roleSetup.getId());
           keycloakService.removeRoleFromKeycloak(roleSetup.getName());
           /**
            * return response on success
            */
           ResponseDTO  response = AppUtils.getResponseDto("role records deleted successfully", HttpStatus.OK);
           return new ResponseEntity<>(response, HttpStatus.OK);

       }catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to fetch all role setups.
     * @return ResponseEntity containing the retrieved roles record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getRoles() {
        try{
            /**
             * retrieving all records
             */
            List<RoleSetup> roleSetups = roleSetupRepo.findAll();
            if (roleSetups.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("no role record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            ResponseDTO  response = AppUtils.getResponseDto("roles records fetched successfully", HttpStatus.OK, roleSetups);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
