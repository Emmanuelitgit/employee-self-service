package employee_self_service.user_service.repo;

import employee_self_service.user_service.models.UserCompany;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCompanyRepo extends JpaRepository<UserCompany, UUID> {
    Optional<UserCompany> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);
}
