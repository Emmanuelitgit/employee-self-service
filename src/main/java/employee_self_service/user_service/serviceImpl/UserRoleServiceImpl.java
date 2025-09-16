package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.RoleSetup;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.models.UserRole;
import employee_self_service.user_service.repo.RoleSetupRepo;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.user_service.repo.UserRoleRepo;
import employee_self_service.user_service.service.UserRoleService;
import employee_self_service.util.AppUtils;
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
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRepo userRepo;
    private final RoleSetupRepo roleSetupRepo;
    private final UserRoleRepo userRoleRepo;

    @Autowired
    public UserRoleServiceImpl(UserRepo userRepo, RoleSetupRepo roleSetupRepo, UserRoleRepo userRoleRepo) {
        this.userRepo = userRepo;
        this.roleSetupRepo = roleSetupRepo;
        this.userRoleRepo = userRoleRepo;
    }

    /**
     * @description This method is used to save/update user role record
     * @param userId the id of the user to assign the role to
     * @param roleId the id of the role
     * @return ResponseEntity containing the saved user role record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> saveUserRole(UUID userId, UUID roleId) {
       try {
           /**
            * checking if user exist
            * checking is selected role exist
            */
           Optional<User> userOptional = userRepo.findById(userId);
           Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
           if (userOptional.isEmpty() || roleSetupOptional.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("user or role record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * check if user has a role already
            */
           Optional<UserRole> userRoleOptional = userRoleRepo.findByUserId(userId);
           /**
            * delete all user existing roles of the user
            */
           userRoleOptional.ifPresent(userRole -> userRoleRepo.deleteAllById(List.of(userRole.getId())));

           /**
            * saving user role record
            */
           UserRole userRole = UserRole
                   .builder()
                   .roleId(roleId)
                   .userId(userId)
                   .build();
           UserRole userRoleResponse = userRoleRepo.save(userRole);
           /**
            * return response on success
            */
           ResponseDTO  response = AppUtils.getResponseDto("users records fetched successfully", HttpStatus.OK, userRoleResponse);
           return new ResponseEntity<>(response, HttpStatus.OK);

       }catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
}
