package employee_self_service.payment_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecipientCreatePayload {
    private String type;
    private String name;
    private String account_number;
    private String bank_code;
    private String currency;
}
