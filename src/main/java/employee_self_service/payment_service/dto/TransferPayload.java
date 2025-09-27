package employee_self_service.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferPayload {
    private String source;
    private Double amount;
    private String reference;
    private String recipient;
    private String reason;
}
