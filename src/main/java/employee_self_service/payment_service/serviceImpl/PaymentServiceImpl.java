package employee_self_service.payment_service.serviceImpl;

import employee_self_service.payment_service.dto.PaymentPayload;
import employee_self_service.payment_service.dto.WebHookPayload;
import employee_self_service.payment_service.models.Payment;
import employee_self_service.payment_service.repo.PaymentRepo;
import employee_self_service.payment_service.service.PaymentService;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.exception.ServerException;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;

    @Autowired
    public PaymentServiceImpl( PaymentRepo paymentRepo) {
        this.paymentRepo = paymentRepo;
    }

    /**
     * @description Fetches all payments records from the database.
     * @return ResponseEntity containing a list of payments records and status information.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> findAll() {
       try{
           log.info("in fetch all payments records");
           List<Payment> payments = paymentRepo.findAll();
           if (payments.isEmpty()){
               throw new NotFoundException("no payment record found");
           }
           ResponseDTO responseDTO = AppUtils.getResponseDto("payment records", HttpStatus.OK, payments);
           return new ResponseEntity<>(responseDTO, HttpStatus.OK);
       } catch (NotFoundException e) {
           throw new NotFoundException(e.getMessage());
       }catch (ServerException e) {
           throw new ServerException(e.getMessage());
       }
    }

    /**
     * @description Saves a new payment record to the database.
     * @param paymentPayload the payment record to save.
     * @return ResponseEntity containing the saved payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> makePayment(PaymentPayload paymentPayload) {

        log.info("Payment process initiated successfully:->>>>");
        ResponseDTO responseDTO = AppUtils.getResponseDto("payment success", HttpStatus.OK);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * @description Updates an existing booking record identified by ID.
     * @param paymentId the ID of the booking to update.
     * @param paymentPayload the updated payment data.
     * @return ResponseEntity containing the updated payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updatePayment(UUID paymentId, PaymentPayload paymentPayload) {
        try{

            ResponseDTO responseDTO = AppUtils.getResponseDto("payment record updated", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        }catch (ServerException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * @description Deletes a payment record based on the given ID.
     * @param paymentId the ID of the payment to delete.
     * @return ResponseEntity indicating whether the deletion was successful.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> removePayment(UUID paymentId) {
        try{
            Payment payment = paymentRepo.findById(paymentId)
                    .orElseThrow(()-> new NotFoundException("no payment record found"));
            paymentRepo.deleteById(payment.getId());

            ResponseDTO responseDTO = AppUtils.getResponseDto("payment record removed successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ServerException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * @description Fetches a specific payment record by ID.
     * @param paymentId the ID of the payment record to retrieve.
     * @return ResponseEntity containing the payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     * */
    @Override
    public ResponseEntity<ResponseDTO> getPaymentById(UUID paymentId) {
        try{
            Payment payment = paymentRepo.findById(paymentId)
                    .orElseThrow(()-> new NotFoundException("no payment record found"));

            ResponseDTO responseDTO = AppUtils.getResponseDto("payment record fetched successfully", HttpStatus.OK, payment);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);
        } catch (NotFoundException e) {
            throw new NotFoundException(e.getMessage());
        } catch (ServerException e) {
            throw new ServerException(e.getMessage());
        }
    }

    /**
     * @description This method is used to listen to updates from payStack.
     * @param webHookPayload the object containing the data to be received from payStack.
     * @return ResponseEntity containing the status code of the operation which will serve as acknowledgement of receiving the data.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     * */
    @Override
    public ResponseEntity<Object> getWebhookData(WebHookPayload webHookPayload) {
        if (webHookPayload != null){
            WebHookPayload.Data data = webHookPayload.getData();
            /**
             * load payment record by reference number
             */
            Optional<Payment> payment = paymentRepo.findByReference(data.getReference());
            if (payment.isPresent() && data.getStatus().equalsIgnoreCase("success")){
                Payment existingData = payment.get();
                /**
                 * save updated payment records
                 */
                existingData.setPaymentStatus(AppConstants.PAID);
                existingData.setTransactionId(data.getId());
                existingData.setCurrency(data.getCurrency());
                existingData.setChannel(data.getChannel());
                existingData.setPaymentDate(data.getPaid_at());
                paymentRepo.save(existingData);

                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return null;
    }
}