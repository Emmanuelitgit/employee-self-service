package employee_self_service.user_service;

import employee_self_service.leave_service.models.UserLeaveBalance;
import employee_self_service.leave_service.repo.UserLeaveBalanceRepo;
import employee_self_service.user_service.dto.*;
import employee_self_service.user_service.external.KeycloakService;
import employee_self_service.user_service.models.*;
import employee_self_service.user_service.repo.*;
import employee_self_service.user_service.serviceImpl.UserServiceImpl;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.management.relation.Role;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private KeycloakService keycloakService;

    @Mock
    private UserRoleRepo userRoleRepo;

    @Mock
    private UserPermissionRepo userPermissionRepo;

    @Mock
    private UserDepartmentRepo userDepartmentRepo;

    @Mock
    private UserDTOProjection userDTOProjection;

    @Mock
    private UserCompanyRepo userCompanyRepo;

    @Mock
    private RoleSetupRepo roleSetupRepo;

    @Mock
    private PermissionSetupRepo permissionSetupRepo;

    @Mock
    private DepartmentRepo departmentRepo;

    @Mock
    private CompanyRepo companyRepo;

    @Mock
    private UserLeaveBalanceRepo userLeaveBalanceRepo;

    @Mock
    private DTOMapper dtoMapper;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldCreateUserSuccessfully(){

        UUID Id = UUID.fromString("c2b15f28-1a31-4d1f-a260-c4d25321cbe5");

        UserPayloadDTO userPayloadDTO = UserPayloadDTO
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .leaveBalance(15.5F)
                .role(Id)
                .companies(List.of(Id))
                .departments(List.of(Id))
                .phone("0597893082")
                .managerId(Id)
                .permissions(List.of(Id))
                .username("eyidana001")
                .password("1234")
                .build();

        RoleSetup roleSetup = RoleSetup
                .builder()
                .name("ADMIN")
                .build();

        User user = User
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .phone("0597893082")
                .managerId(Id)
                .username("eyidana001")
                .password("1234")
                .build();

        UserDTO userDTO = UserDTO
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .phone("0597893082")
                .username("eyidana001")
                .build();

        Department department = Department
                .builder()
                .companyId(Id)
                .Id(Id)
                .managerId(Id)
                .name("Applications")
                .build();

        Company company = Company
                .builder()
                .name("STL")
                .Id(Id)
                .build();

        PermissionSetup permissionSetup = PermissionSetup
                .builder()
                .id(Id)
                .name("CREATE_USER")
                .build();

        UserDepartment userDepartment = UserDepartment
                .builder()
                .departmentId(Id)
                .userId(Id)
                .Id(Id)
                .build();

        UserCompany userCompany = UserCompany
                .builder()
                .Id(Id)
                .companyId(Id)
                .userId(Id)
                .build();

        UserPermission userPermission = UserPermission
                .builder()
                .permissionId(Id)
                .userId(Id)
                .id(Id)
                .build();

        UserRole userRole = UserRole
                .builder()
                .roleId(Id)
                .userId(Id)
                .build();

        UserLeaveBalance userLeaveBalance = UserLeaveBalance
                .builder()
                .usrId(Id)
                .leaveBalance(15.5F)
                .id(Id)
                .build();


        when(userRepo.save(user)).thenReturn(user);
        when(dtoMapper.toUserEntity(userPayloadDTO)).thenReturn(user);
        when(dtoMapper.toUserDTO(user, "ADMIN")).thenReturn(userDTO);
        doNothing().when(keycloakService).saveUserToKeycloak(userPayloadDTO);
        when(userRoleRepo.save(any(UserRole.class))).thenReturn(userRole);
        when(userPermissionRepo.save(any(UserPermission.class))).thenReturn(userPermission);
        when(userDepartmentRepo.save(any(UserDepartment.class))).thenReturn(userDepartment);
        when(userCompanyRepo.save(any(UserCompany.class))).thenReturn(userCompany);
        when(roleSetupRepo.findById(Id)).thenReturn(Optional.of(roleSetup));
        when(permissionSetupRepo.findById(Id)).thenReturn(Optional.of(permissionSetup));
        when(departmentRepo.findById(Id)).thenReturn(Optional.of(department));
        when(companyRepo.findById(Id)).thenReturn(Optional.of(company));
        when(userLeaveBalanceRepo.findByUsrId(Id)).thenReturn(Optional.of(userLeaveBalance));
        when(userLeaveBalanceRepo.save(any(UserLeaveBalance.class))).thenReturn(userLeaveBalance);

        ResponseEntity<ResponseDTO> response = userService.createUser(userPayloadDTO);

        assert response.getBody()!=null;
        assertEquals(HttpStatus.CREATED, response.getStatusCode());

        verify(userRepo).save(user);
        verify(roleSetupRepo, times(2)).findById(Id);
    }

    @DisplayName("Simulating the update user method")
    @Test
    void shouldUpdateUserSuccessfully(){

        UUID Id = UUID.fromString("c2b15f28-1a31-4d1f-a260-c4d25321cbe5");


        UpdateUserPayload updateUserPayload = UpdateUserPayload
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .leaveBalance(15.5F)
                .role(Id)
                .companies(List.of(Id))
                .departments(List.of(Id))
                .phone("0597893082")
                .managerId(Id)
                .permissions(List.of(Id))
                .username("eyidana001")
                .build();

        RoleSetup roleSetup = RoleSetup
                .builder()
                .name("ADMIN")
                .build();

        User user = User
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .phone("0597893082")
                .managerId(Id)
                .username("eyidana001")
                .password("1234")
                .build();

        UserDTO userDTO = UserDTO
                .builder()
                .id(Id)
                .email("eyidana001@gmail.com")
                .firstName("Emmanuel")
                .lastName("Yidana")
                .phone("0597893082")
                .username("eyidana001")
                .build();

        Department department = Department
                .builder()
                .companyId(Id)
                .Id(Id)
                .managerId(Id)
                .name("Applications")
                .build();

        Company company = Company
                .builder()
                .name("STL")
                .Id(Id)
                .build();

        PermissionSetup permissionSetup = PermissionSetup
                .builder()
                .id(Id)
                .name("CREATE_USER")
                .build();

        UserDepartment userDepartment = UserDepartment
                .builder()
                .departmentId(Id)
                .userId(Id)
                .Id(Id)
                .build();

        UserCompany userCompany = UserCompany
                .builder()
                .Id(Id)
                .companyId(Id)
                .userId(Id)
                .build();

        UserPermission userPermission = UserPermission
                .builder()
                .permissionId(Id)
                .userId(Id)
                .id(Id)
                .build();

        UserRole userRole = UserRole
                .builder()
                .roleId(Id)
                .userId(Id)
                .build();

        UserLeaveBalance userLeaveBalance = UserLeaveBalance
                .builder()
                .usrId(Id)
                .leaveBalance(15.5F)
                .id(Id)
                .build();


        when(userRepo.findById(Id)).thenReturn(Optional.of(user));
        when(userRepo.save(user)).thenReturn(user);
        doNothing().when(keycloakService).updateUserInKeycloak(updateUserPayload);
        when(dtoMapper.toUserDTO(user, "ADMIN")).thenReturn(userDTO);
        when(userRoleRepo.save(any(UserRole.class))).thenReturn(userRole);
        when(userPermissionRepo.save(any(UserPermission.class))).thenReturn(userPermission);
        when(userDepartmentRepo.save(any(UserDepartment.class))).thenReturn(userDepartment);
        when(userCompanyRepo.save(any(UserCompany.class))).thenReturn(userCompany);
        when(roleSetupRepo.findById(Id)).thenReturn(Optional.of(roleSetup));
        when(permissionSetupRepo.findById(Id)).thenReturn(Optional.of(permissionSetup));
        when(departmentRepo.findById(Id)).thenReturn(Optional.of(department));
        when(companyRepo.findById(Id)).thenReturn(Optional.of(company));
        when(userLeaveBalanceRepo.findByUsrId(Id)).thenReturn(Optional.of(userLeaveBalance));
        when(userLeaveBalanceRepo.save(any(UserLeaveBalance.class))).thenReturn(userLeaveBalance);

        ResponseEntity<ResponseDTO> response = userService.updateUser(updateUserPayload, Id);

        assert response.getBody()!=null;
        assertEquals(HttpStatus.OK, response.getStatusCode());

        verify(userRepo).save(user);
        verify(roleSetupRepo, times(2)).findById(Id);
    }

    @DisplayName("Simulating the find user by id method")
    @Test
    void shouldFindUserById(){
        UUID Id = UUID.fromString("c2b15f28-1a31-4d1f-a260-c4d25321cbe5");
    }
}
