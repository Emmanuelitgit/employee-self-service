package employee_self_service.loan_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "loan_tbl", schema = "loan_schema")
public class Loan extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private Double amountToBorrow;
    private Double amountPaid;
    private Double amountRemaining;
    private String status;
    private String loanType;
    private String nextOfKin;
    private UUID userId;
    private LocalDate expectedPaymentDate;
    private LocalDate datePaid;
    private String remarks;
    private String reasonForLoan;
    private String paymentStatus;
    private UUID managerId;
    private String bankAccountNumber;
    private String bankName;
    private String bankBranch;
}
