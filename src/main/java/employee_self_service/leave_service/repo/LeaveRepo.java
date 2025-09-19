package employee_self_service.leave_service.repo;

import employee_self_service.leave_service.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRepo extends JpaRepository<Leave, UUID> {

    @Query(value = "SELECT * FROM leave_tbl WHERE user_id=:userId", nativeQuery = true)
    List<Leave> fetchLeavesForLoggedInUser(@Param("userId") UUID userId);

    @Query(value = "SELECT * FROM leave_tbl " +
            "WHERE user_id=:managerId AND status='PENDING_MANAGER_APPROVAL'", nativeQuery = true)
    List<Leave> fetchLeavesForManager(@Param("managerId") UUID managerId);

    @Query(value = "SELECT * FROM leave_tbl l " +
            "JOIN user_company_tbl uc on uc.user_id=l.user_id " +
            "JOIN company_setup_tbl c ON c.id=uc.company_id " +
            "WHERE c.hr_id=:hrId AND l.status='PENDING_HR_APPROVAL'", nativeQuery = true)
    List<Leave> fetchLeavesForHR(@Param("hrId") UUID hrId);
}
