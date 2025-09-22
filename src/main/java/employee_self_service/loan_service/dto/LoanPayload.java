package employee_self_service.loan_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoanPayload {
    private UUID id;
    @NotNull(message = "Amount to borrow cannot be null")
    private Double amountToBorrow;
    private Double amountPaid;
    private Double amountRemaining;
    private String status;
    @NotBlank(message = "Loan type cannot be null or empty")
    private String loanType;
    @NotBlank(message = "Next of kin cannot be null or empty")
    private String nextOfKin;
    private UUID userId;
    @NotNull(message = "Expected payment date cannot be null or empty")
    private String expectedPaymentDate;
    private String datePaid;
    private String remarks;
    @NotBlank(message = "Reason for loan cannot be null or empty")
    private String reasonForLoan;
    private String paymentStatus;
}
