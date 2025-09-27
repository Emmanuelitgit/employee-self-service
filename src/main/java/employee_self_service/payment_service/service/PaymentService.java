package employee_self_service.payment_service.service;

import employee_self_service.payment_service.dto.PaymentPayload;
import employee_self_service.payment_service.dto.WebHookPayload;
import employee_self_service.payment_service.models.Payment;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface PaymentService {
    ResponseEntity<ResponseDTO> findAll();
    ResponseEntity<ResponseDTO> makePayment(PaymentPayload paymentPayload);
    ResponseEntity<ResponseDTO> updatePayment(UUID paymentId, PaymentPayload paymentPayload);
    ResponseEntity<ResponseDTO> removePayment(UUID paymentId);
    ResponseEntity<ResponseDTO> getPaymentById(UUID paymentId);
    ResponseEntity getWebhookData(WebHookPayload webHookPayload);
}
