package employee_self_service.payment_service.rest;

import employee_self_service.payment_service.dto.AcceptPaymentPayload;
import employee_self_service.payment_service.dto.WebHookPayload;
import employee_self_service.payment_service.serviceImpl.PaymentServiceImpl;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentRest {

    private final PaymentServiceImpl paymentService;

    @Autowired
    public PaymentRest(PaymentServiceImpl paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> findAll(){
        return paymentService.findAll();
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> makePayment(@RequestBody AcceptPaymentPayload acceptPaymentPayload){
        return paymentService.acceptPayment(acceptPaymentPayload);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> updatePayment(@PathVariable UUID paymentId,  @RequestBody AcceptPaymentPayload acceptPaymentPayload){
        return paymentService.updatePayment(paymentId, acceptPaymentPayload);
    }

    @GetMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> getPaymentById(@PathVariable UUID paymentId){
        return paymentService.getPaymentById(paymentId);
    }

    @DeleteMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> removePayment(@PathVariable UUID paymentId){
        return paymentService.getPaymentById(paymentId);
    }
    @PostMapping("/webhook")
    public ResponseEntity<Object> getWebhookData(@RequestBody WebHookPayload webHookPayload){
        return paymentService.getWebhookData(webHookPayload);
    }

    @PostMapping("/disburse/loan/{loanId}")
    public ResponseEntity<ResponseDTO> disburseLoan(@PathVariable UUID loanId){
        return paymentService.disburseLoan(loanId);
    }

    @PostMapping("/create-recipient/{userId}")
    public ResponseEntity<ResponseDTO> createRecipient(@PathVariable UUID userId){
        return paymentService.createRecipient(userId);
    }
}
