package employee_self_service.payment_service.service;

import employee_self_service.payment_service.dto.AcceptPaymentPayload;
import employee_self_service.payment_service.dto.WebHookPayload;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PaymentService {
    ResponseEntity<ResponseDTO> findAll();
    ResponseEntity<ResponseDTO> acceptPayment(AcceptPaymentPayload acceptPaymentPayload);
    ResponseEntity<ResponseDTO> updatePayment(UUID paymentId, AcceptPaymentPayload acceptPaymentPayload);
    ResponseEntity<ResponseDTO> removePayment(UUID paymentId);
    ResponseEntity<ResponseDTO> getPaymentById(UUID paymentId);
    ResponseEntity getWebhookData(WebHookPayload webHookPayload);
}
