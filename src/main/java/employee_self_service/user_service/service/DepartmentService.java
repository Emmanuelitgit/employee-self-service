package employee_self_service.user_service.service;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.dto.UserPayloadDTO;
import employee_self_service.user_service.models.Department;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface DepartmentService {
    ResponseEntity<ResponseDTO> createDepartment(Department department);
    ResponseEntity<ResponseDTO> getDepartments();
    ResponseEntity<ResponseDTO> getDepartmentById(UUID departmentId);
    ResponseEntity<ResponseDTO> updateDepartment(Department department, UUID departmentId);
    ResponseEntity<ResponseDTO> removeDepartment(UUID departmentId);
}
