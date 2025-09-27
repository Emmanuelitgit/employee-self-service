package employee_self_service.payment_service.dto;

import lombok.Data;

@Data
public class WebHookPayload {
    private Data data;

    @lombok.Data
    public static class Data{
        private String status;
        private String paid_at;
        private String currency;
        private Long id;
        private String channel;
        private String reference;
    }
}
