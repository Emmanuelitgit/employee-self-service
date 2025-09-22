package employee_self_service.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDTO {
    private LocalDate endDate;
    private Long leaveDays;
    private String status;
    private LocalDate startDate;
    private String leaveType;
    private String leaveNumber;
    private UUID userId;
}
