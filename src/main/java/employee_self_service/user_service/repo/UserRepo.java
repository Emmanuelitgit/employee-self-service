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

    @Query(value = "SELECT BIN_TO_UUID(u.id) FROM  user_tbl WHERE username=:username", nativeQuery = true)
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
            "WHERE c.hr_id=:hrId ", nativeQuery = true)
    List<UserDTOProjection> fetchEmployeesForHR(@Param("hrId") UUID gmId);

    @Query(value = "SELECT BIN_TO_UUID(u.id) AS id, u.first_name, " +
            "u.last_name, u.email, u.phone, u.username, rs.name AS role " +
            "FROM user_tbl u " +
            "JOIN user_company_tbl uc ON uc.user_id=u.id " +
            "JOIN company_setup_tbl c ON c.id=uc.company_id " +
            "WHERE c.gm_id=:gmId ", nativeQuery = true)
    List<UserDTOProjection> fetchEmployeesForGM(@Param("gmId") UUID gmId);

    @Query(value = "SELECT BIN_TO_UUID(c.hr_id) FROM user_tbl u " +
            "JOIN user_company_tbl uc ON uc.user_id=u.id " +
            "JOIN company_setup_tbl c ON uc.company_id=c.id " +
            "WHERE u.id=:employeeId ", nativeQuery = true)
    UUID getHRIdByEmployeeId(@Param("employeeId") UUID employeeId);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByEmail(String email);
}
