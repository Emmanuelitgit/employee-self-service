package employee_self_service.leave_service.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeavePayload {
    private UUID id;
    private UUID userId;
    private Long leaveDays;
    @NotNull(message = "Leave start date cannot be null or empty")
    private String startDate;
    @NotNull(message = "Leave end date cannot be null or empty")
    private String endDate;
    private String status;
    @NotBlank(message = "Leave type cannot be null or empty")
    private String leaveType;//annual leave, maternal leave, sick leave
    private UUID managerId;
    private String leaveNumber;
}
