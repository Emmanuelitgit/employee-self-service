package employee_self_service.leave_service.repo;

import employee_self_service.leave_service.models.UserLeaveBalance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserLeaveBalanceRepo extends JpaRepository<UserLeaveBalance, UUID> {

    @Modifying
    @Query(value = "UPDATE leave_balance_tbl SET leave_balance = leave_balance + :leaveBalance " +
            "WHERE user_id IS NOT NULL", nativeQuery = true)
    void updateLeaveBalance(@Param("leaveBalance") float leaveBalance);

    Optional<UserLeaveBalance> findByUsrId(UUID usrId);
}
