package employee_self_service.user_service.repo;

import employee_self_service.user_service.models.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DepartmentRepo extends JpaRepository<Department, UUID> {

    @Query(value = "SELECT * FROM department_tbl d " +
            "JOIN company_setup_tbl c ON d.company_id=c.id " +
            "WHERE c.hr_id=:hrId ", nativeQuery = true)
    List<Department> fetchDepartmentsForHR(@Param("hrId") UUID hrId);

    @Query(value = "SELECT * FROM department_tbl WHERE manager_id=:managerId", nativeQuery = true)
    List<Department> fetchDepartmentsForManager(@Param("managerId") UUID managerId);

    @Query(value = "SELECT * FROM department_tbl d " +
            "JOIN company_setup_tbl c ON d.company_id=c.id " +
            "WHERE c.general_manager_id=:generalManagerId ", nativeQuery = true)
    List<Department> fetchDepartmentsForGM(@Param("generalManager") UUID generalManager);
}
