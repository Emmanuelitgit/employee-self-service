package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Department;
import employee_self_service.user_service.repo.DepartmentRepo;
import employee_self_service.user_service.service.DepartmentService;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepo departmentRepo;
    private final AppUtils appUtils;

    @Autowired
    public DepartmentServiceImpl(DepartmentRepo departmentRepo, AppUtils appUtils) {
        this.departmentRepo = departmentRepo;
        this.appUtils = appUtils;
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> createDepartment(Department department) {
        try {
            log.info("In create department method:->>>{}", department);
            ResponseDTO responseDTO;
            /**
             * validate payload
             */
            if (department == null) {
                responseDTO = AppUtils.getResponseDto("Department payload cannot be null", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
            /**
             * save and return response
             */
            log.info("About to save department record");
            Department departmentRes = departmentRepo.save(department);
            log.info("Department record saved successfully:->>{}", departmentRes);
            responseDTO = AppUtils.getResponseDto("Department added", HttpStatus.CREATED, departmentRes);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}",
                    500, e.getCause(), e.getMessage());
            ResponseDTO response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> getDepartments() {
        try {
            log.info("In get all departments method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            log.info("About to fetch departments from db");
            List<Department> departments = departmentRepo.findAll();
            if (departments.isEmpty()) {
                responseDTO = AppUtils.getResponseDto("No department record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.info("Departments fetched successfully");
            responseDTO = AppUtils.getResponseDto("Departments", HttpStatus.OK, departments);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}",
                    500, e.getCause(), e.getMessage());
            ResponseDTO response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getDepartmentById(UUID departmentId) {
        try {
            log.info("In get department by id method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            log.info("About load department record from db");
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.info("Department record fetched from db successfully");
            responseDTO = AppUtils.getResponseDto("Department record", HttpStatus.OK, departmentOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}",
                    500, e.getCause(), e.getMessage());
            ResponseDTO response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> updateDepartment(Department department, UUID departmentId) {
        try {
            log.info("In update department method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            log.info("About to load existing department record from db");
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * save updated record
             */
            log.info("About to save updated record");
            Department existingDepartment = departmentOptional.get();
            existingDepartment.setName(department.getName() != null ? department.getName() : existingDepartment.getName());
            Department departmentRes = departmentRepo.save(existingDepartment);

            /**
             * return response on success
             */
            log.info("Department updated successfully:->>{}", departmentRes);
            responseDTO = AppUtils.getResponseDto("Department record", HttpStatus.OK, departmentRes);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}",
                    500, e.getCause(), e.getMessage());
            ResponseDTO response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> removeDepartment(UUID departmentId) {
        try {
            log.info("In remove department method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            log.info("About to load department record from db");
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * delete record
             */
            log.info("About to delete department record");
            departmentRepo.deleteById(departmentId);
            /**
             * return response on success
             */
            log.info("Department deleted successfully:");
            responseDTO = AppUtils.getResponseDto("Department record deleted", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}",
                    500, e.getCause(), e.getMessage());
            ResponseDTO response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to fetch departments for logged-in user(HR, GM, Manager)
     * @return ResponseEntity containing the departments list and status info
     * @auther Emmanuel Yidana
     * @createdAt 20th August 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN','GENERAL_MANAGER','MANAGER')")
    @Override
    public ResponseEntity<ResponseDTO> fetchDepartmentsForManagerOrHROrGM() {
        try {
            log.info("In fetch departments for HR or Manager or GM");
            ResponseDTO responseDTO;
            String userRole = appUtils.getAuthenticatedUserRole();
            log.info("Fetching user role from principal:->>{}", userRole);
            UUID userId = UUID.fromString(appUtils.getAuthenticatedUserId());
            log.info("Fetching user id from principal");

            /**
             * fetch departments base on the logged-in user role
             */
            List<Department> departments = new ArrayList<>();
            if (AppConstants.MANAGER_ROLE.equalsIgnoreCase(userRole)){
                log.info("About to fetch department for Manager");
                List<Department> departmentsFromDB = departmentRepo.fetchDepartmentsForManager(userId);
                departments.addAll(departmentsFromDB);
            } else if (AppConstants.HR_ROLE.equalsIgnoreCase(userRole)) {
                log.info("About to fetch department for HR");
                List<Department> departmentsFromDB = departmentRepo.fetchDepartmentsForHR(userId);
                departments.addAll(departmentsFromDB);
            } else if (AppConstants.GENERAL_MANAGER_ROLE.equalsIgnoreCase(userRole)) {
                log.info("About to fetch department for General Manager");
                List<Department> departmentsFromDB = departmentRepo.fetchDepartmentsForGM(userId);
                departments.addAll(departmentsFromDB);
            }else {
                log.error("User not authorized to this feature");
                responseDTO = AppUtils.getResponseDto("User not authorized to this feature", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }
            /**
             * return response on success
             */
            log.info("Departments fetched successfully");
            ResponseDTO  response = AppUtils.getResponseDto("Departments fetched successfully", HttpStatus.OK, departments);
            return new ResponseEntity<>(response, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
