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
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> saveRole(RoleSetup roleSetup) {
      try {
          log.info("In create role record method");
          ResponseDTO responseDTO;
          /**
           * validating payload
           */
          if (roleSetup == null){
              log.error("Role payload is null:->>");
              responseDTO = AppUtils.getResponseDto("Payload cannot be null", HttpStatus.BAD_REQUEST);
              return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
          }
          /**
           * saving record to db and keycloak
           */
          log.info("About to save role to db");
          RoleSetup roleSetupRes = roleSetupRepo.save(roleSetup);
          log.info("About to save role to keycloak");
          keycloakService.saveRoleToKeycloak(roleSetup);

          /**
           * return response on success
           */
          log.info("Role record saved successfully");
          responseDTO = AppUtils.getResponseDto("role record added successfully", HttpStatus.CREATED, roleSetupRes);
          return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateRole(RoleSetup roleSetup, UUID roleId) {
        try {
            log.info("In update role record method:->>{}", roleSetupRepo);
            ResponseDTO responseDTO;
            /**
             * check if role exist
             */
            log.info("About to load existing role record from db");
            Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
            if (roleSetupOptional.isEmpty()){
                log.error("Role record not found:->>{}", roleId);
                responseDTO = AppUtils.getResponseDto("Role record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            RoleSetup existingRoleSetup = roleSetupOptional.get();

            /**
             * saving record
             */
            log.info("About to update role record in keycloak");
            existingRoleSetup.setName(roleSetup.getName() != null? roleSetup.getName():existingRoleSetup.getName());
            RoleSetup res = roleSetupRepo.save(existingRoleSetup);
            log.info("About to update role in keycloak");
            keycloakService.updateRole(existingRoleSetup.getName(), roleSetup.getName());
            /**
             * return response on success
             */
            log.info("Role record updated successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("role records updated successfully", HttpStatus.OK, roleSetup);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

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
            log.info("In fetch role by id method");
            ResponseDTO responseDTO;
            /**
             * check if record exist
             */
            log.info("About to load role record from db");
            Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
            if (roleSetupOptional.isEmpty()){
                log.error("Role record not found:->>{}", roleId);
                responseDTO = AppUtils.getResponseDto("Role record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Role record fetched successfully:->>{}", roleSetupOptional.get());
            RoleSetup roleSetup = roleSetupOptional.get();
            responseDTO = AppUtils.getResponseDto("role records fetched successfully", HttpStatus.OK, roleSetup);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> deleteRole(UUID roleId) {
       try {
           log.info("In deleted role record method:->>{}", roleId);
           ResponseDTO responseDTO;
           /**
            * check if record exist
            */
           Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
           if (roleSetupOptional.isEmpty()) {
               log.error("Role record not found:->>{}", roleId);
               responseDTO = AppUtils.getResponseDto("Role record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
           }
           RoleSetup roleSetup = roleSetupOptional.get();
           /**
            * delete record from db and keycloak
            */
           log.info("About to delete role record from db");
           roleSetupRepo.deleteById(roleSetup.getId());
           log.info("About to delete role record from keycloak");
           keycloakService.removeRoleFromKeycloak(roleSetup.getName());
           /**
            * return response on success
            */
           log.info("Role record deleted successfully");
           responseDTO = AppUtils.getResponseDto("role records deleted successfully", HttpStatus.OK);
           return new ResponseEntity<>(responseDTO, HttpStatus.OK);

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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> getRoles() {
        try{
            log.info("In fetch roles method");
            ResponseDTO responseDTO;
            /**
             * retrieving all records
             */
            log.info("About to load roles from db");
            List<RoleSetup> roleSetups = roleSetupRepo.findAll();
            if (roleSetups.isEmpty()){
                log.error("No role record found");
                responseDTO = AppUtils.getResponseDto("No role record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Roles fetched successfully");
            responseDTO = AppUtils.getResponseDto("Roles records fetched successfully", HttpStatus.OK, roleSetups);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
