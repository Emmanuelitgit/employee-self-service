package employee_self_service.user_service.repo;

import employee_self_service.user_service.models.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRoleRepo extends JpaRepository<UserRole, UUID> {
}
