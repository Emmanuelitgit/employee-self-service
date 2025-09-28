package employee_self_service.payment_service.dto;

import lombok.Data;

@Data
public class AcceptPaymentResponse {
    private String status;
    private String message;
    private Data data;

    @lombok.Data
    public static class Data{
    private String authorization_url;
    private String access_code;
    private String reference;

}
}
