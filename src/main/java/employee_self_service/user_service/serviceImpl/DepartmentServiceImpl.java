package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Department;
import employee_self_service.user_service.repo.DepartmentRepo;
import employee_self_service.user_service.service.DepartmentService;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepo departmentRepo;

    public DepartmentServiceImpl(DepartmentRepo departmentRepo) {
        this.departmentRepo = departmentRepo;
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
            Department departmentRes = departmentRepo.save(department);
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
            List<Department> departments = departmentRepo.findAll();
            if (departments.isEmpty()) {
                responseDTO = AppUtils.getResponseDto("No department record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
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
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
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
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * save updated record
             */
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
            Optional<Department> departmentOptional = departmentRepo.findById(departmentId);
            if (departmentOptional.isEmpty()) {
                log.error("Department record not found:->>>{}", departmentId);
                responseDTO = AppUtils.getResponseDto("Department record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * delete record
             */
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
}
