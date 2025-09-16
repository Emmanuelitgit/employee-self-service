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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;

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

    @Override
    public ResponseEntity<ResponseDTO> saveUserRole(UUID userId, UUID roleId) {
       try {
           Optional<User> userOptional = userRepo.findById(userId);
           Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);

           if (userOptional.isEmpty() || roleSetupOptional.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("user or role record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }

           UserRole userRole = new UserRole();
           userRole.setRoleId(roleId);
           userRole.setUserId(userId);

           UserRole userRoleResponse = userRoleRepo.save(userRole);

           ResponseDTO  response = AppUtils.getResponseDto("users records fetched successfully", HttpStatus.OK, userRoleResponse);
           return new ResponseEntity<>(response, HttpStatus.OK);
       }catch (Exception e) {
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }
}
