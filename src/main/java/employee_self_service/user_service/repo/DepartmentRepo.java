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

    @Query(value = "SELECT d.* FROM department_tbl d " +
            "JOIN company_setup_tbl c ON d.company_id=c.id " +
            "WHERE c.id IN (:hrCompaniesIds) ", nativeQuery = true)
    List<Department> fetchDepartmentsForHR(@Param("hrId") List<UUID> hrCompaniesIds);

    @Query(value = "SELECT * FROM department_tbl WHERE manager_id=:managerId", nativeQuery = true)
    List<Department> fetchDepartmentsForManager(@Param("managerId") UUID managerId);

    @Query(value = "SELECT d.* FROM department_tbl d " +
            "JOIN company_setup_tbl c ON d.company_id=c.id " +
            "WHERE c.id IN (:gmCompaniesIds) ", nativeQuery = true)
    List<Department> fetchDepartmentsForGM(@Param("gmCompaniesIds") List<UUID> gmCompaniesIds);
}
