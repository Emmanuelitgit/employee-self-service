package employee_self_service.leave_service.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leave_balance_tbl", schema = "leave_schema")
public class UserLeaveBalance {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "user_id", nullable = false)
    private UUID usrId;
    @Column(name = "leave_balance", nullable = false)
    private Float leaveBalance;
}
