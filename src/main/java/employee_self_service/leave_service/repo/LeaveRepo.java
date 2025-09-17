package employee_self_service.leave_service.repo;

import employee_self_service.leave_service.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LeaveRepo extends JpaRepository<Leave, UUID> {
}
