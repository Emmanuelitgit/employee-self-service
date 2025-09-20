package employee_self_service.notification_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LeaveDTO {
    private String endDate;
    private Integer leaveDays;
    private String status;
    private String startDate;
    private String leaveType;
    private String leaveNumber;
    private UUID userId;
}
