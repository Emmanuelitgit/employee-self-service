package employee_self_service.user_service.serviceImpl;


import employee_self_service.user_service.dto.*;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.external.KeycloakService;
import employee_self_service.user_service.models.RoleSetup;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.repo.RoleSetupRepo;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.user_service.repo.UserRoleRepo;
import employee_self_service.user_service.service.UserService;
import employee_self_service.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final UserRoleRepo userRoleRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, DTOMapper dtoMapper, PasswordEncoder passwordEncoder, UserRoleServiceImpl userRoleServiceImpl, RoleSetupRepo roleSetupRepo, RoleSetupServiceImpl roleSetupServiceImpl, KeycloakService keycloakService, UserRoleRepo userRoleRepo) {
        this.userRepo = userRepo;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.userRoleServiceImpl = userRoleServiceImpl;
        this.roleSetupRepo = roleSetupRepo;
        this.roleSetupServiceImpl = roleSetupServiceImpl;
        this.keycloakService = keycloakService;
        this.userRoleRepo = userRoleRepo;
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
               log.error("User payload cannot be null");
               ResponseDTO  response = AppUtils.getResponseDto("User payload cannot be null", HttpStatus.BAD_REQUEST);
               return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
           }
           /**
            * check if email already exist
            */
           Optional<User> userEmailExist =  userRepo.findUserByEmail(userPayloadDTO.getEmail());
           if (userEmailExist.isPresent()){
               log.error("Email already exist:->>>{}", userPayloadDTO.getEmail());
               ResponseDTO  response = AppUtils.getResponseDto("Email already exist", HttpStatus.ALREADY_REPORTED);
               return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
           }
           /**
            * check if username already exist
            */
           Optional<User> usernameExist =  userRepo.findUserByUsername(userPayloadDTO.getUsername());
           if (usernameExist.isPresent()){
               log.error("Username already exist:->>>{}", userPayloadDTO.getUsername());
               ResponseDTO  response = AppUtils.getResponseDto("Username already exist", HttpStatus.ALREADY_REPORTED);
               return new ResponseEntity<>(response, HttpStatus.ALREADY_REPORTED);
           }
           /**
            * saving user to db
            */
           userPayloadDTO.setPassword(passwordEncoder.encode(userPayloadDTO.getPassword()));
           User user = dtoMapper.toUserEntity(userPayloadDTO);
           User userResponse = userRepo.save(user);

           /**
            * saving user role to db
            */
           userRoleServiceImpl.saveUserRole(userResponse.getId(), userPayloadDTO.getRole());

           log.info("User added to db");

           log.info("About to save user in keycloak");
           keycloakService.saveUserToKeycloak(userPayloadDTO);

           /**
            * return response on success
            */
           String roleName = roleSetupRepo.findById(userPayloadDTO.getRole())
                   .orElseThrow(()->new NotFoundException("Role record not found")).getName();
           UserDTO userDTO = DTOMapper.toUserDTO(userResponse, roleName);
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
     * @return ResponseEntity containing the retrieved users and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUsers() {
       try{
           log.info("In get all users method:->>>{}",
                   SecurityContextHolder.getContext().getAuthentication().getName());
           /**
            * fetching all users from db
            */
           List<UserDTOProjection> users = userRepo.getUsersDetails();
           if (users.isEmpty()){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * return response on success
            */
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
     * @return ResponseEntity containing the retrieved user and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getUserById(UUID userId) {
       try{
           log.info("In get user by id method:->>>>>>");
           /**
            * retrieving user record from db
            */
           UserDTOProjection user = userRepo.getUsersDetailsByUserId(userId);
           if (user == null){
               ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * return response on success
            */
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
     * @return ResponseEntity containing the updated user and status info
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

            /**
             * update fields and save
             */
            existingData.setEmail(userPayload.getEmail() !=null ? userPayload.getEmail() : existingData.getEmail());
            existingData.setFirstName(userPayload.getFirstName() !=null ? userPayload.getFirstName() : existingData.getFirstName());
            existingData.setLastName(userPayload.getLastName() !=null ? userPayload.getLastName() : existingData.getLastName());
            existingData.setUsername(userPayload.getUsername() !=null ? userPayload.getUsername() : existingData.getUsername());
            existingData.setPhone(userPayload.getPhone() !=null ? userPayload.getPhone() : existingData.getPhone());
            User userResponse = userRepo.save(existingData);

            /**
             * getting role name from the role setup db
             */
           RoleSetup role =  new RoleSetup();
            if (userPayload.getRole() != null){
                RoleSetup roleData  = roleSetupRepo.findById(userPayload.getRole())
                        .orElseThrow(()-> new NotFoundException("role record not found"));
                role.setName(roleData.getName());
            }

            /**
             * update user role in db
             */
            userRoleServiceImpl.saveUserRole(userResponse.getId(), userPayload.getRole());
            /**
             * update user in keycloak
             */
            keycloakService.updateUserInKeycloak(userPayload);

            log.info("user updated successfully:->>>{}", userResponse);
            /**
             * return response on success
             */
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
     * @return ResponseEntity containing message and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> removeUser(UUID userId) {
        try {
            log.info("In remove user method:->>>>>>");
            /**
             * check if user exist by id
             */
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                ResponseDTO  response = AppUtils.getResponseDto("no user record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            /**
             * delete user record
             */
            userRepo.deleteById(userId);
            keycloakService.removeUserFromKeyCloak(userOptional.get().getEmail());
            log.info("user removed successfully:->>>>>>");
            /**
             * return response on success
             */
            ResponseDTO  response = AppUtils.getResponseDto("user record removed successfully", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
