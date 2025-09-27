package employee_self_service.payment_service.dto;

import lombok.Data;

@Data
public class CreateRecipientResponse {
    private Boolean status;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data{
        private String recipient_code;
    }
}
