package employee_self_service.user_service.repo;

import employee_self_service.user_service.models.RoleSetup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoleSetupRepo extends JpaRepository<RoleSetup, UUID> {

    @Query(value = "SELECT name FROM role_setup_tbl " +
            "WHERE name=LOWER(:name) OR name=UPPER(:name) OR name=:name ", nativeQuery = true)
    String getRoleSetupByName(@Param("name") String name);
}
