package employee_self_service.leave_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "leave_tbl", schema = "leave_schema")
public class Leave extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false, name = "user_id")
    private UUID userId;
    @Column(nullable = false, name = "leave_days")
    private Long leaveDays;
    @Column(nullable = false, name = "start_date")
    private LocalDate startDate;
    @Column(nullable = false, name = "end_date")
    private LocalDate endDate;
    @Column(nullable = false, name = "status")
    private String status;
    @Column(nullable = false, name = "leave_type")
    private String leaveType;//annual leave, maternal leave, sick leave
    @Column(name = "manager_id", nullable = false)
    private UUID managerId;
    @Column(name = "leave_number")
    private String leaveNumber;
}
