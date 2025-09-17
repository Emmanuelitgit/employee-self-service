package employee_self_service.leave_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_tbl", schema = "user_schema")
public class Leave extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private Integer leaveDays;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status;
    private String leaveType;//annual leave, maternal leave, sick leave
}
