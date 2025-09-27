package employee_self_service.payment_service.rest;

import employee_self_service.payment_service.dto.PaymentPayload;
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
    public ResponseEntity<ResponseDTO> makePayment(@RequestBody PaymentPayload paymentPayload){
        return paymentService.makePayment(paymentPayload);
    }

    @PutMapping("/{paymentId}")
    public ResponseEntity<ResponseDTO> updatePayment(@PathVariable UUID paymentId,  @RequestBody PaymentPayload paymentPayload){
        return paymentService.updatePayment(paymentId, paymentPayload);
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
}
