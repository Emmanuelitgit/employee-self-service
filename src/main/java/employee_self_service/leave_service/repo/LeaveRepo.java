package employee_self_service.leave_service.repo;

import employee_self_service.leave_service.models.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface LeaveRepo extends JpaRepository<Leave, UUID> {

    @Query(value = "SELECT * FROM leave_tbl WHERE user_id=:userId", nativeQuery = true)
    List<Leave> fetchLeavesForLoggedInUser(@Param("userId") UUID userId);

    @Query(value = "SELECT * FROM leave_tbl " +
            "WHERE manager_id=:managerId " +
            "AND status='PENDING_MANAGER_APPROVAL' ", nativeQuery = true)
    List<Leave> fetchLeavesForManager(@Param("managerId") UUID managerId);

    @Query(value = "SELECT l.* FROM leave_tbl l " +
            "JOIN user_company_tbl uc on uc.user_id=l.user_id " +
            "JOIN company_setup_tbl c ON c.id=uc.company_id " +
            "WHERE c.id IN (:hrCompaniesIds) AND " +
            "l.status='PENDING_HR_APPROVAL' ", nativeQuery = true)
    List<Leave> fetchLeavesForHR(@Param("hrCompaniesIds") List<UUID> hrCompaniesIds);

    @Query(value = "SELECT COUNT(*) FROM leave_tbl " +
            "WHERE (start_date BETWEEN :startDate " +
            "AND :endDate AND user_id=:userId " +
            "AND status NOT IN ('CANCELLED', 'APPROVED', 'REJECTED')) " +
            "OR (end_date BETWEEN :startDate " +
            "AND :endDate AND user_id=:userId " +
            "AND status NOT IN ('CANCELLED', 'APPROVED', 'REJECTED')) ", nativeQuery = true)
    Integer checkOverLapping(@Param("userId") UUID userId,
                             @Param("startDate") LocalDate startDate,
                             @Param("endDate") LocalDate endDate);
}
