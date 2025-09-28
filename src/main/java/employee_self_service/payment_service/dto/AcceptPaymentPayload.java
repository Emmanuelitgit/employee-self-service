package employee_self_service.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AcceptPaymentPayload {
    private String email;
    private Double amount;
    private String paymentType;
    private UUID loanId;
}
