package employee_self_service.user_service.repo;

import employee_self_service.user_service.dto.UserDTOProjection;
import employee_self_service.user_service.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name,u.last_name, u.email, u.phone, u.username, rs.name AS role FROM user_tbl u " +
            "JOIN user_role_tbl ur ON u.id = ur.user_id " +
            "JOIN role_setup_tbl rs ON ur.role_id=rs.id ", nativeQuery = true)
    List<UserDTOProjection> getUsersDetails();


    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name, u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tbl u " +
            "JOIN user_role_tbl ur ON u.id = ur.user_id " +
            "JOIN role_setup_tbl rs ON ur.role_id = rs.id " +
            "WHERE u.id =? ", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserId(UUID userId);


    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name, u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tbl u " +
            "JOIN user_role_tbl ur ON u.id = ur.user_id " +
            "JOIN role_setup_tbl rs ON ur.role_id = rs.id " +
            "WHERE u.email =? ", nativeQuery = true)
    UserDTOProjection getUsersDetailsByUserEmail(String email);

    @Query(value = "SELECT rs.name AS role FROM user_role_tbl ur " +
            "JOIN user_tbl u ON u.id=ur.user_id " +
            "JOIN role_setup_tbl rs ON rs.id=ur.role_id " +
            "WHERE u.email =? ", nativeQuery = true)
    String getUserRole(String username);

    @Query(value = "SELECT BIN_TO_UUID(u.id) FROM  user_tbl u WHERE username=:username", nativeQuery = true)
    UUID getUserId(@RequestParam("username") String username);

    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, " +
            "u.first_name, u.last_name, u.email, u.phone, " +
            "u.username, rs.name AS role " +
            "FROM user_tbl WHERE manager_id=:managerId ", nativeQuery = true)
    List<UserDTOProjection> fetchEmployeesForManager(@Param("managerId") UUID managerId);

    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name, " +
            "u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tbl u " +
            "JOIN user_company_tbl uc ON uc.user_id=u.id " +
            "JOIN company_setup_tbl c ON c.id=uc.company_id " +
            "WHERE c.id IN (:hrCompaniesIds) ", nativeQuery = true)
    List<UserDTOProjection> fetchEmployeesForHR(@Param("hrCompaniesIds") List<UUID> hrCompaniesIds);

    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name, " +
            "u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tbl u " +
            "JOIN user_company_tbl uc ON uc.user_id=u.id " +
            "JOIN company_setup_tbl c ON c.id=uc.company_id " +
            "WHERE c.id IN (:gmCompaniesIds) ", nativeQuery = true)
    List<UserDTOProjection> fetchEmployeesForGM(@Param("gmCompaniesIds") List<UUID> gmCompaniesIds);

    @Query(value = "SELECT BIN_TO_UUID(uc.user_id) FROM user_company_tbl uc " +
            "JOIN user_role_tbl ur ON uc.user_id=ur.user_id " +
            "JOIN role_setup_tbl rs ON ur.role_id=rs.id " +
            "WHERE rs.name='HR' AND uc.company_id=:companyId ", nativeQuery = true)
    UUID getHRIdByEmployeeCompanyId(@Param("companyId") UUID companyId);

    @Query(value = "SELECT BIN_TO_UUID(u.manager_id) FROM user_tbl u WHERE u.id=:userId ", nativeQuery = true)
    UUID getManagerId(@Param("userId") UUID userId);

    @Query(value = "SELECT BIN_TO_UUID(company_id) FROM user_company_tbl where user_id=:hrId ", nativeQuery = true)
    List<UUID> getHRCompaniesIds(@Param("hrId") UUID hrId);

    @Query(value = "SELECT BIN_TO_UUID(company_id) FROM user_company_tbl where user_id=:gmId ", nativeQuery = true)
    List<UUID> getGMCompaniesIds(@Param("gmId") UUID gmId);

    @Query(value = "SELECT BIN_TO_UUID(company_id) FROM user_company_tbl where user_id=:employeeId ", nativeQuery = true)
    UUID getEmployeeCompanyId(@Param("employeeId") UUID employeeId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);
}
