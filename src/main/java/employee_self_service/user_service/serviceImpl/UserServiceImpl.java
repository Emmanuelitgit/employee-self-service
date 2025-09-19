package employee_self_service.user_service.serviceImpl;


import employee_self_service.leave_service.models.UserLeaveBalance;
import employee_self_service.leave_service.repo.UserLeaveBalanceRepo;
import employee_self_service.user_service.dto.*;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.external.KeycloakService;
import employee_self_service.user_service.models.*;
import employee_self_service.user_service.repo.*;
import employee_self_service.user_service.service.UserService;
import employee_self_service.util.AppUtils;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    private final RoleSetupRepo roleSetupRepo;
    private final RoleSetupServiceImpl roleSetupServiceImpl;
    private final KeycloakService keycloakService;
    private final UserRoleRepo userRoleRepo;
    private final DepartmentRepo departmentRepo;
    private final UserDepartmentRepo userDepartmentRepo;
    private final UserCompanyRepo userCompanyRepo;
    private final CompanyRepo companyRepo;
    private final PermissionSetupRepo permissionSetupRepo;
    private final UserPermissionRepo userPermissionRepo;
    private final UserLeaveBalanceRepo userLeaveBalanceRepo;

    @Autowired
    public UserServiceImpl(UserRepo userRepo, DTOMapper dtoMapper, PasswordEncoder passwordEncoder, RoleSetupRepo roleSetupRepo, RoleSetupServiceImpl roleSetupServiceImpl, KeycloakService keycloakService, UserRoleRepo userRoleRepo, DepartmentRepo departmentRepo, DepartmentRepo departmentRepo1, UserDepartmentRepo userDepartmentRepo, UserCompanyRepo userCompanyRepo, CompanyRepo companyRepo, PermissionSetupRepo permissionSetupRepo, UserPermissionRepo userPermissionRepo, UserLeaveBalanceRepo userLeaveBalanceRepo) {
        this.userRepo = userRepo;
        this.dtoMapper = dtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleSetupRepo = roleSetupRepo;
        this.roleSetupServiceImpl = roleSetupServiceImpl;
        this.keycloakService = keycloakService;
        this.userRoleRepo = userRoleRepo;
        this.departmentRepo = departmentRepo1;
        this.userDepartmentRepo = userDepartmentRepo;
        this.userCompanyRepo = userCompanyRepo;
        this.companyRepo = companyRepo;
        this.permissionSetupRepo = permissionSetupRepo;
        this.userPermissionRepo = userPermissionRepo;
        this.userLeaveBalanceRepo = userLeaveBalanceRepo;
    }

    /**
     * @description This method is used to save new user record to the db
     * @param userPayloadDTO
     * @return ResponseEntity containing the saved user record and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
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
           log.info("About to save user to db");
           userPayloadDTO.setPassword(passwordEncoder.encode(userPayloadDTO.getPassword()));
           User user = dtoMapper.toUserEntity(userPayloadDTO);
           User userResponse = userRepo.save(user);
           /**
            * saving user role, user departments and user companies
            */
           log.info("About to save user role");
           saveUserRole(userResponse.getId(), userPayloadDTO.getRole());
           log.info("About to save user permissions");
           saveUserPermission(userResponse.getId(),userPayloadDTO.getPermissions());
           log.info("About to save user companies");
           saveUserCompanies(userPayloadDTO.getCompanies(), userResponse.getId());
           log.info("About to save user departments");
           saveUserDepartments(userPayloadDTO.getDepartments(), userResponse.getId());
           if (userPayloadDTO.getLeaveBalance()!=null){
               log.info("About to save user leave balance");
               saveUserLeaveBalance(userPayloadDTO.getLeaveBalance(), userResponse.getId());
           }
           /**
            * saving user in keycloak
            */
           log.info("About to save user in keycloak");
           keycloakService.saveUserToKeycloak(userPayloadDTO);
           /**
            * return response on success
            */
           String roleName = null;
           if (userPayloadDTO.getRole()!=null){
               roleName = roleSetupRepo.findById(userPayloadDTO.getRole())
                       .orElseThrow(()->new NotFoundException("Role record not found")).getName();
           }
           UserDTO userDTO = DTOMapper.toUserDTO(userResponse, roleName);
           log.info("User created successfully:->>{}", userResponse);
           ResponseDTO  response = AppUtils.getResponseDto("User record added successfully", HttpStatus.CREATED, userDTO);
           return new ResponseEntity<>(response, HttpStatus.CREATED);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    /**
     * @description This method is used to get all users from the db
     * @return ResponseEntity containing the retrieved users and status info
     * @auther Emmanuel Yidana
     * @createdAt 16th August 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> getUsers() {
       try{
           log.info("In get all users method:->>>{}",
                   SecurityContextHolder.getContext().getAuthentication().getName());
           /**
            * fetching all users from db
            */
           log.info("About to fetch users from db");
           List<UserDTOProjection> users = userRepo.getUsersDetails();
           if (users.isEmpty()){
               log.error("No user record found");
               ResponseDTO  response = AppUtils.getResponseDto("No user record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * return response on success
            */
           log.info("Users fetched successfully");
           ResponseDTO  response = AppUtils.getResponseDto("Users records fetched successfully", HttpStatus.OK, users);
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
           log.info("About to load user record from db");
           UserDTOProjection user = userRepo.getUsersDetailsByUserId(userId);
           if (user == null){
               log.error("User record not found:->>{}", userId);
               ResponseDTO  response = AppUtils.getResponseDto("User record not found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
           }
           /**
            * return response on success
            */
           log.info("User records fetched successfully:->>{}", user);
           ResponseDTO  response = AppUtils.getResponseDto("User records fetched successfully", HttpStatus.OK, user);
           return new ResponseEntity<>(response, HttpStatus.OK);

       } catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> updateUser(UpdateUserPayload userPayload, UUID userId) {
        try{
            log.info("In update user method:->>{}", userPayload);
            /**
             * check if user exist
             */
            log.info("About to load user records from db");
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                log.error("User record not found:->>{}", userId);
                ResponseDTO  response = AppUtils.getResponseDto("User record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }

            /**
             * update fields and save
             */
            log.info("About to update user records in db");
            User existingData = userOptional.get();
            existingData.setEmail(userPayload.getEmail() !=null ? userPayload.getEmail() : existingData.getEmail());
            existingData.setFirstName(userPayload.getFirstName() !=null ? userPayload.getFirstName() : existingData.getFirstName());
            existingData.setLastName(userPayload.getLastName() !=null ? userPayload.getLastName() : existingData.getLastName());
            existingData.setUsername(userPayload.getUsername() !=null ? userPayload.getUsername() : existingData.getUsername());
            existingData.setPhone(userPayload.getPhone() !=null ? userPayload.getPhone() : existingData.getPhone());
            existingData.setManagerId(userPayload.getManagerId()!=null?userPayload.getManagerId() : existingData.getManagerId());
            User userResponse = userRepo.save(existingData);

            /**
             * saving user role, user departments and user companies
             */
            log.info("About to update user role");
            saveUserRole(userResponse.getId(), userPayload.getRole());
            log.info("About to update user permissions");
            saveUserPermission(userResponse.getId(),userPayload.getPermissions());
            log.info("About to save update companies");
            saveUserCompanies(userPayload.getCompanies(), userResponse.getId());
            log.info("About to update user departments");
            saveUserDepartments(userPayload.getDepartments(), userResponse.getId());
            if (userPayload.getLeaveBalance()!=null){
                log.info("About to update user leave balance");
                saveUserLeaveBalance(userPayload.getLeaveBalance(), userResponse.getId());
            }
            /**
             * update user in keycloak
             */
            log.info("About to update user in keycloak");
            keycloakService.updateUserInKeycloak(userPayload);
            /**
             * return response on success
             */
            String roleName = null;
            if (userPayload.getRole()!=null){
                roleName = roleSetupRepo.findById(userPayload.getRole())
                        .orElseThrow(()->new NotFoundException("Role record not found")).getName();
            }
            UserDTO userDTOResponse = DTOMapper.toUserDTO(userResponse, roleName);
            log.info("User updated successfully:->>{}", userResponse);
            ResponseDTO  response = AppUtils.getResponseDto("User records updated successfully", HttpStatus.OK, userDTOResponse);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
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
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Transactional
    @Override
    public ResponseEntity<ResponseDTO> removeUser(UUID userId) {
        try {
            log.info("In remove user method");
            /**
             * check if user exist by id
             */
            log.info("About to load user records from db");
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                log.error("User record not found:->>{}", userId);
                ResponseDTO  response = AppUtils.getResponseDto("User record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
            }
            /**
             * delete user record
             */
            log.info("About to remove user departments");
            userDepartmentRepo.deleteByUserId(userId);
            log.info("About to remove user companies");
            userCompanyRepo.deleteByUserId(userId);
            log.info("About to remove user from db");
            userRepo.deleteById(userId);
            log.info("About to remove user from keycloak");
            keycloakService.removeUserFromKeyCloak(userOptional.get().getEmail());

            /**
             * return response on success
             */
            log.info("User removed successfully");
            ResponseDTO  response = AppUtils.getResponseDto("user record removed successfully", HttpStatus.OK);
            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description A helper method used to save user departments
     * @param departmentIds The ids of departments to be assigned to user
     * @param userId The id of the user
     */
    private void saveUserDepartments(List<UUID> departmentIds, UUID userId){
        /**
         * remove all departments associated with the user if exist
         */
        Optional<UserDepartment> userDepartmentOptional = userDepartmentRepo.findByUserId(userId);
        userDepartmentOptional.ifPresent((dep)->userDepartmentRepo.deleteById(dep.getId()));

        departmentIds.forEach((departmentId)->{
            /**
             * check if department exist
             */
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()){
                log.error("Department record not found for:->>{}", departmentId);
                throw new NotFoundException("Department record not found");
            }

            /**
             * building payload to be saved
             */
            UserDepartment userDepartment = UserDepartment
                    .builder()
                    .departmentId(departmentId)
                    .userId(userId)
                    .build();

            /**
             * saving record
             */
            userDepartmentRepo.save(userDepartment);
        });
    }

    /**
     * @description A helper method used to save user companies
     * @param companiesIds The ids of companies to be assigned to user
     * @param userId The id of the user
     */
    private void saveUserCompanies(List<UUID> companiesIds, UUID userId){
        /**
         * remove all companies associated with the user if exist
         */
        Optional<UserCompany> userCompanyOptional = userCompanyRepo.findByUserId(userId);
        userCompanyOptional.ifPresent((company)->userCompanyRepo.deleteById(company.getId()));

        companiesIds.forEach((companyId)->{
            /**
             * check if department exist
             */
            Optional<Company> companyOptional = companyRepo.findById(companyId);
            if (companyOptional.isEmpty()){
                log.error("Company record not found for:->>{}", companyId);
                throw new NotFoundException("Company record not found");
            }

            /**
             * building payload to be saved
             */
            UserCompany userCompany = UserCompany
                    .builder()
                    .companyId(companyId)
                    .userId(userId)
                    .build();

            /**
             * saving record
             */
            userCompanyRepo.save(userCompany);
        });
    }

    /**
     * @description A helper method used tom save user role
     * @param userId The id of the user
     * @param roleId The id of the role to be assigned to user
     */
    private void saveUserRole(UUID userId, UUID roleId) {
        /**
         * checking if selected role exist
         */
        Optional<RoleSetup> roleSetupOptional = roleSetupRepo.findById(roleId);
        if (roleSetupOptional.isEmpty()){
            log.error("Role record not found:->>{}", roleId);
            throw new NotFoundException("Role record not found");
        }

        /**
         * delete all existing roles of the user
         */
        Optional<UserRole> userRoleOptional = userRoleRepo.findByUserId(userId);
        userRoleOptional.ifPresent(userRole -> userRoleRepo.deleteAllById(List.of(userRole.getId())));

        /**
         * saving new user role record
         */
        UserRole userRole = UserRole
                .builder()
                .roleId(roleId)
                .userId(userId)
                .build();
        userRoleRepo.save(userRole);
    }

    /**
     * @description A helper method used tom save user role
     * @param userId The id of the user
     * @param permissionIds The ids of the permissions to be assigned to user
     */
    private void saveUserPermission(UUID userId, List<UUID> permissionIds) {
        /**
         * delete all existing permissions of the user
         */
        Optional<UserPermission> userPermissionOptional = userPermissionRepo.findByUserId(userId);
        userPermissionOptional.ifPresent((perm)->userPermissionRepo.deleteById(perm.getUserId()));

        permissionIds.forEach((perm)->{
            /**
             * check if permission exist
             */
            Optional<PermissionSetup> permissionSetup = permissionSetupRepo.findById(perm);
            if (permissionSetup.isEmpty()){
                log.error("Permission record not found:->>{}", perm);
                throw new NotFoundException("Permission record not found");
            }
            /**
             * saving record
             */
            UserPermission userPermission = UserPermission
                    .builder()
                    .permissionId(perm)
                    .userId(userId)
                    .build();
            userPermissionRepo.save(userPermission);
        });
    }

    /**
     * @description A helper method used to save user leave balance on account creation/update
     * @param leaveBalance The leave balance to be saved
     * @param userId The id of the user
     */
    @Transactional
    protected void saveUserLeaveBalance(float leaveBalance, UUID userId){
        /**
         * remove the existing record of the user if exist
         */
        Optional<UserLeaveBalance> balanceOptional = userLeaveBalanceRepo.findByUsrId(userId);
        balanceOptional.ifPresent((bal)->userLeaveBalanceRepo.deleteById(bal.getId()));

        /**
         * prepare payload to save new leave balance
         */
        UserLeaveBalance userLeaveBalance = UserLeaveBalance
                .builder()
                .leaveBalance(leaveBalance)
                .usrId(userId)
                .build();
        userLeaveBalanceRepo.save(userLeaveBalance);
    }
}
