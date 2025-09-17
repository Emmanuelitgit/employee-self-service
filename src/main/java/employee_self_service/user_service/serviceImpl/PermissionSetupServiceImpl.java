package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.PermissionSetup;
import employee_self_service.user_service.repo.PermissionSetupRepo;
import employee_self_service.user_service.service.PermissionSetupService;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PermissionSetupServiceImpl implements PermissionSetupService {
    private final PermissionSetupRepo permissionSetupRepo;

    @Autowired
    public PermissionSetupServiceImpl(PermissionSetupRepo permissionSetupRepo) {
        this.permissionSetupRepo = permissionSetupRepo;
    }

    /**
     * @description This method is used to save a new permission setup record
     * @param permissionSetup The payload to be saved
     * @return ResponseEntity containing the saved record and status info
     * @auther Emmanuel Yidana
     * @createdAt 17th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createPermission(PermissionSetup permissionSetup) {
        try {
            log.info("In create permission method:->>{}", permissionSetup);
            ResponseDTO responseDTO;
            /**
             * validate payload
             */
            if (permissionSetup==null){
                log.error("Permission payload is null");
                responseDTO = AppUtils.getResponseDto("Permission payload cannot be null", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
            /**
             * save record
             */
            log.info("About to save permission record");
            PermissionSetup res = permissionSetupRepo.save(permissionSetup);
            /**
             * return response on success
             */
            log.info("Permission saved successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Permission saved successfully", HttpStatus.CREATED, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrive all permission records
     * @return ResponseEntity containing the permissions and status info
     * @auther Emmanuel Yidana
     * @createdAt 17th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getPermissions() {
        try {
            log.info("In fetch all permissions method");
            ResponseDTO responseDTO;
            /**
             * load permissions from db
             */
            log.info("About to fetch permissions from db");
            List<PermissionSetup> permissionSetups = permissionSetupRepo.findAll();
            if (permissionSetups.isEmpty()){
                log.error("No permission setup record found");
                responseDTO = AppUtils.getResponseDto("No permission record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Permissions retrieved successfully");
            responseDTO = AppUtils.getResponseDto("Permissions retrieved successfully", HttpStatus.OK, permissionSetups);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrieve a permission record by id
     * @param permissionId The id of the permission to be retrieved
     * @return ResponseEntity containing the retrieved record and status info
     * @createdAt 17th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getPermissionById(UUID permissionId) {
        try {
            log.info("In fetch permission by id method:->>{}", permissionId);
            ResponseDTO responseDTO;

            /**
             * check if permission exist
             */
            log.info("About to fetch permission record from db");
            Optional<PermissionSetup> permissionOptional = permissionSetupRepo.findById(permissionId);
            if (permissionOptional.isEmpty()){
                log.error("Permission setup record not found");
                responseDTO = AppUtils.getResponseDto("Permission record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.error("Permission record retrieved successfully:->>{}", permissionOptional.get());
            responseDTO = AppUtils.getResponseDto("Permission record retrieved", HttpStatus.OK, permissionOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to update permission record by id
     * @param permissionSetup The payload to be updated
     * @param permissionId The id of the permission to be updated
     * @return ResponseEntity containing the updated record and status info
     * @auther Emmanuel Yidana
     * @createdAt 17 August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updatePermission(PermissionSetup permissionSetup, UUID permissionId) {
        try {
            log.info("In update permission method:->>{}", permissionSetup);
            ResponseDTO responseDTO;

            /**
             * check if permission exist
             */
            log.info("About to load existing permission record from db");
            Optional<PermissionSetup> permissionOptional = permissionSetupRepo.findById(permissionId);
            if (permissionOptional.isEmpty()){
                log.error("Permission setup record not found");
                responseDTO = AppUtils.getResponseDto("Permission record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * saving updated record
             */
            log.info("About to update permission record");
            PermissionSetup existingData = permissionOptional.get();
            existingData.setName(permissionSetup.getName()!=null? permissionSetup.getName() : existingData.getName());
            PermissionSetup res = permissionSetupRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Permission record updated successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Permission record updated", HttpStatus.OK, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> removePermission(UUID permissionId) {
        try {
            log.info("In delete permission:->>{}", permissionId);
            ResponseDTO responseDTO;

            /**
             * check if permission record exist
             */
            log.info("About to fetch permission record from db");
            Optional<PermissionSetup> permissionOptional = permissionSetupRepo.findById(permissionId);
            if (permissionOptional.isEmpty()){
                log.error("Permission setup record not found");
                responseDTO = AppUtils.getResponseDto("Permission record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * delete remove
             */
            log.info("About to delete permission record:->>{}", permissionId);
            permissionSetupRepo.deleteById(permissionId);

            /**
             *
             */
            log.info("Permission record deleted successfully");
            responseDTO = AppUtils.getResponseDto("Permission record deleted", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
