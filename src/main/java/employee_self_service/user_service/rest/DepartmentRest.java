package employee_self_service.user_service.rest;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Department;
import employee_self_service.user_service.serviceImpl.DepartmentServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Department Management")
@RestController
@RequestMapping("/api/v1/departments")
public class DepartmentRest {
    private final DepartmentServiceImpl departmentService;

    public DepartmentRest(DepartmentServiceImpl departmentService) {
        this.departmentService = departmentService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createDepartment(Department department){
        return departmentService.createDepartment(department);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getDepartments(){
        return departmentService.getDepartments();
    }

    @GetMapping("/{departmentId}")
    public ResponseEntity<ResponseDTO> getDepartmentById(@PathVariable UUID departmentId){
        return departmentService.getDepartmentById(departmentId);
    }

    @PutMapping("/{departmentId}")
    public ResponseEntity<ResponseDTO> updateDepartment(@RequestBody Department department,
                                                        @PathVariable UUID departmentId){
        return departmentService.updateDepartment(department, departmentId);
    }

    @DeleteMapping("/{departmentId}")
    public ResponseEntity<ResponseDTO> removeDepartment(@PathVariable UUID departmentId){
        return departmentService.removeDepartment(departmentId);
    }

    @GetMapping("/for-logged-in-user")
    public ResponseEntity<ResponseDTO> fetchDepartmentsForManagerOrHROrGM(){
        return departmentService.fetchDepartmentsForManagerOrHROrGM();
    }
}
