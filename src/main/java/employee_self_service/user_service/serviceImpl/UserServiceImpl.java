package employee_self_service.user_service.serviceImpl;


import employee_self_service.user_service.dto.*;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.external.KeycloakService;
import employee_self_service.user_service.models.RoleSetup;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.repo.RoleSetupRepo;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.user_service.service.UserService;
import employee_self_service.util.AppUtils;
import org.springframework.kafka.core.KafkaTemplate;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final DTOMapper dtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleServiceImpl userRoleServiceImpl;
    private final RoleSetupRepo roleSetupRepo;
    private final RoleSetupServiceImpl roleSetupServiceImpl;
    private final KeycloakService keycloakService;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, DTOMapper dtoMapper, PasswordEncoder passwordEncoder, UserRoleServiceImpl userRoleServiceImpl, RoleSetupRepo roleSetupRepo, RoleSetupServiceImpl roleSetupServiceImpl, KeycloakService keycloakService) {
        this.userRepo = userRepo;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRoleServiceImpl = userRoleServiceImpl;
        this.roleSetupRepo = roleSetupRepo;
        this.roleSetupServiceImpl = roleSetupServiceImpl;
        this.keycloakService = keycloakService;
    }

    /**
     * @description This method is used to save new user record to the db
     * @param userPayloadDTO
     * @return ResponseEntity containing the saved user record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> createUser(UserPayloadDTO userPayloadDTO) {
       try {
           log.info("In create user method:->>>{}", userPayloadDTO);
           /**
            * validating payload
            */
           if (userPayloadDTO  == null){
               ResponseDTO  response = AppUtils.getResponseDto("user payload cannot be null", HttpStatus.BAD_REQUEST);
               return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
           }
           /**
            * check if email already exist
            */
           Optional<User> userEmailExist =  userRepo.findUserByEmail(userPayloadDTO.getEmail());
           if (userEmailExist.isPresent()){
               ResponseDTO  response = AppUtils.getResponseDto("email already exist", HttpStatus.ALREADY_REPORTED);
               return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
           }
           /**
            * check if username already exist
            */
           Optional<User> usernameExist =  userRepo.findUserByUsername(userPayloadDTO.getUsername());
           if (usernameExist.isPresent()){
               ResponseDTO  response = AppUtils.getResponseDto("username already exist", HttpStatus.ALREADY_REPORTED);
               return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
           }
           /**
            * saving user to db
            */
           userPayloadDTO.setPassword(passwordEncoder.encode(userPayloadDTO.getPassword()));
           User user = dtoMapper.toUserEntity(userPayloadDTO);
           User userResponse = userRepo.save(user);
           /**
            * check if selected role exist
            */
           Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(userPayloadDTO.getRole());
           if (roleSetupOptional.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("role record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * saving user role to db
            */
           RoleSetup role = roleSetupOptional.get();
           userRoleServiceImpl.saveUserRole(userResponse.getId(), userPayloadDTO.getRole());

           log.info("User added to db");

           log.info("About to save user in keycloak");
           keycloakService.saveUserToKeycloak(userPayloadDTO);

           /**
            * return response on success
            */
           UserDTO userDTO = DTOMapper.toUserDTO(userResponse, role.getName());
           ResponseDTO  response = AppUtils.getResponseDto("user record added successfully", HttpStatus.CREATED, userDTO);
           return new ResponseEntity<>(response, HttpStatus.CREATED);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to get all users from the db
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUsers() {
       try{
           log.info("In get all users method:->>>>>>");
           List<UserDTOProjection> users = userRepo.getUsersDetails();
           if (users.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           ResponseDTO  response = AppUtils.getResponseDto("users records fetched successfully", HttpStatus.OK, users);
           return new ResponseEntity<>(response, HttpStatus.OK);
       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to get user records given the user id.
     * @param userId
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUserById(UUID userId) {
       try{
           log.info("In get user by id method:->>>>>>");
           UserDTOProjection user = userRepo.getUsersDetailsByUserId(userId);
           if (user == null){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           ResponseDTO  response = AppUtils.getResponseDto("user records fetched successfully", HttpStatus.OK, user);
           return new ResponseEntity<>(response, HttpStatus.OK);
       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to update user records.
     * @param userPayload
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 27h April 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateUser(UserPayloadDTO userPayload, UUID userId) {
        try{
            log.info("In update user method:->>>>>>{}", userPayload);
            User existingData = userRepo.findById(userId)
                    .orElseThrow(()-> new NotFoundException("user record not found"));

            existingData.setEmail(userPayload.getEmail() !=null ? userPayload.getEmail() : existingData.getEmail());
            existingData.setFirstName(userPayload.getFirstName() !=null ? userPayload.getFirstName() : existingData.getFirstName());
            existingData.setLastName(userPayload.getLastName() !=null ? userPayload.getLastName() : existingData.getLastName());
            existingData.setUsername(userPayload.getUsername() !=null ? userPayload.getUsername() : existingData.getUsername());
            existingData.setPhone(userPayload.getPhone() !=null ? userPayload.getPhone() : existingData.getPhone());
            User userResponse = userRepo.save(existingData);

            // getting the role name from the role setup db
           RoleSetup role =  new RoleSetup();
            if (userPayload.getRole() != null){
                RoleSetup roleData  = roleSetupRepo.findById(userPayload.getRole())
                        .orElseThrow(()-> new NotFoundException("role record not found"));
                role.setName(roleData.getName());
            }

            // saving user in keycloak
            keycloakService.updateUserInKeycloak(userPayload);

            log.info("user updated successfully:->>>>>>");
            UserDTO userDTOResponse = DTOMapper.toUserDTO(userResponse, role.getName());
            ResponseDTO  response = AppUtils.getResponseDto("user records updated successfully", HttpStatus.OK, userDTOResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to remove user records from the db.
     * @param userId
     * @return
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> removeUser(UUID userId) {
        try {
            log.info("In remove user method:->>>>>>");
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            userRepo.deleteById(userId);
            keycloakService.removeUserFromKeyCloak(userOptional.get().getEmail());
            log.info("user removed successfully:->>>>>>");
            ResponseDTO  response = AppUtils.getResponseDto("user record removed successfully", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
