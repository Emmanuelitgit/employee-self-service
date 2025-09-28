package employee_self_service.payment_service.serviceImpl;

import employee_self_service.loan_service.models.Loan;
import employee_self_service.loan_service.repo.LoanRepo;
import employee_self_service.payment_service.dto.*;
import employee_self_service.payment_service.models.Payment;
import employee_self_service.payment_service.repo.PaymentRepo;
import employee_self_service.payment_service.service.PaymentService;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.user_service.exception.ServerException;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepo paymentRepo;
    private final LoanRepo loanRepo;
    private final UserRepo userRepo;
    private final RestTemplate restTemplate;

    @Value("${PAYSTACK_RECIPIENT_ENDPOINT}")
    private String PAYSTACK_RECIPIENT_ENDPOINT;

    @Value("${PAYSTACK_SECRET}")
    private String PAYSTACK_SECRET;

    @Value("${PAYSTACK_PAYMENT_TRANSFER_ENDPOINT}")
    private String PAYSTACK_PAYMENT_TRANSFER_ENDPOINT;

    @Value("${PAYSTACK_ACCEPT_PAYMENT_ENDPOINT}")
    private String PAYSTACK_ACCEPT_PAYMENT_ENDPOINT;

    @Value("${PAYSTACK_VERIFY_ACCOUNT_NUMBER_ENDPOINT}")
    private String PAYSTACK_VERIFY_ACCOUNT_NUMBER_ENDPOINT;

    @Value("${PAYSTACK_TELECOS_LIST_ENDPOINT}")
    private String PAYSTACK_TELECOS_LIST_ENDPOINT;

    @Autowired
    public PaymentServiceImpl(PaymentRepo paymentRepo, LoanRepo loanRepo, UserRepo userRepo, RestTemplate restTemplate) {
        this.paymentRepo = paymentRepo;
        this.loanRepo = loanRepo;
        this.userRepo = userRepo;
        this.restTemplate = restTemplate;
    }

    /**
     * @description Fetches all payments records from the database.
     * @return ResponseEntity containing a list of payments records and status information.
     * @author Emmanuel Yidana
     * @createdAt 27th, September 20255
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
     * @param acceptPaymentPayload the payment record to save.
     * @return ResponseEntity containing the saved payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> acceptPayment(AcceptPaymentPayload acceptPaymentPayload) {
      try {
          log.info("In accept payment method");
          ResponseDTO responseDTO;

          /**
           * headers
           */
          HttpHeaders headers = new HttpHeaders();
          headers.setContentType(MediaType.APPLICATION_JSON);
          headers.setBearerAuth(PAYSTACK_SECRET);

          /**
           * request
           */
          HttpEntity entity = new HttpEntity(acceptPaymentPayload, headers);
          ResponseEntity<AcceptPaymentResponse> response = restTemplate.postForEntity(
                  PAYSTACK_ACCEPT_PAYMENT_ENDPOINT, entity, AcceptPaymentResponse.class
          );

          /**
           * handles unsuccessfully request
           */
          if (!response.getStatusCode().is2xxSuccessful()){
              log.info("Unexpected error occurred:->>{}", response.getBody().getMessage());
              responseDTO = AppUtils.getResponseDto("Error occurred", HttpStatus.BAD_REQUEST);
              return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
          }

          /**
           * building payload
           */
          Payment payment = Payment
                  .builder()
                  .paymentDate(LocalDate.now().toString())
                  .paymentStatus(AppConstants.PENDING)
                  .amount(acceptPaymentPayload.getAmount())
                  .paymentType(acceptPaymentPayload.getPaymentType())
                  .source("EMPLOYEE")
                  .access_code(response.getBody().getData().getAccess_code())
                  .reference(response.getBody().getData().getReference())
                  .authorizationUrl(response.getBody().getData().getAuthorization_url())
                  .entityId(acceptPaymentPayload.getLoanId())
                  .build();
          paymentRepo.save(payment);

          /**
           * return response on success
           */
          log.info("Payment process initiated successfully:->>>>");
          responseDTO = AppUtils.getResponseDto("payment success", HttpStatus.OK, response.getBody());
          return new ResponseEntity<>(responseDTO, HttpStatus.OK);

      }catch (Exception e) {
          log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
          ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
          return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
      }
    }

    /**
     * @description This method is used to disburse a loan to a user
     * @param loanId the id of the loan to be disbursed.
     * @return ResponseEntity containing the saved payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, September 2025
     */
    public ResponseEntity<ResponseDTO> disburseLoan(UUID loanId) {
        log.info("In disburse loan method:->>{}", loanId);
        ResponseDTO responseDTO;

        /**
         * load the loan record by id
         */
        Optional<Loan> loanOptional = loanRepo.findById(loanId);
        if (loanOptional.isEmpty()){
            log.error("Loan record cannot be found:->>{}", loanId);
            responseDTO = AppUtils.getResponseDto("Loan record does not exist", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
        Loan loan = loanOptional.get();

        /**
         * load the user details
         */
        Optional<User> userOptional = userRepo.findById(loan.getUserId());
        if (userOptional.isEmpty()){
            log.error("User record cannot be found:->>{}", loan.getUserId());
            responseDTO = AppUtils.getResponseDto("User record does not exist", HttpStatus.NOT_FOUND);
            return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
        }
        User user = userOptional.get();

        /**
         * check if recipient code is generated
         */
        if (user.getRecipientCode()==null){
            log.error("Recipient code not found");
            responseDTO = AppUtils.getResponseDto("Recipient code not found", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
        }

        /**
         * building payload
         */
        TransferPayload payload = TransferPayload
                .builder()
                .amount(loan.getAmountToBorrow())
                .source("balance")
                .reason(loan.getReasonForLoan())
                .recipient(user.getRecipientCode())
                .reference(loan.getId().toString())
                .build();

        /**
         * headers
         */
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        headers.setBearerAuth(PAYSTACK_SECRET);
//        HttpEntity entity = new HttpEntity(payload, headers);

        /**
         * request
         */
//       ResponseEntity<Object> response = restTemplate.postForEntity(PAYSTACK_PAYMENT_TRANSFER_ENDPOINT, entity, Object.class);
//       if (!response.getStatusCode().is2xxSuccessful()){
//           log.error("Unexpected error occurs");
//           responseDTO = AppUtils.getResponseDto("Transfer fails", HttpStatus.BAD_REQUEST);
//           return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
//       }

        loan.setStatus(AppConstants.DISBURSED);
        Loan res = loanRepo.save(loan);
        /**
         * return response on success
         */
        log.info("Loan disbursed successfully:->>{}", res);
        responseDTO = AppUtils.getResponseDto("Loan disbursed successfully", HttpStatus.OK, res);
        return new ResponseEntity<>(responseDTO, HttpStatus.OK);
    }

    /**
     * @description This method is used to crate a payment recipient
     * @param userId the id of the user to be created as recipient.
     * @return ResponseEntity containing the saved payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, September 2025
     */
    public ResponseEntity<ResponseDTO> createRecipient(UUID userId){
        try {
            log.info("In create recipient method:->>{}", userId);
            ResponseDTO responseDTO;

            /**
             * load the user record by id
             */
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                log.error("User record cannot be found:->>{}", userId);
                responseDTO = AppUtils.getResponseDto("User record does not exist", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            User user = userOptional.get();

            /**
             * building payload
             */
            RecipientCreatePayload payload = RecipientCreatePayload
                    .builder()
                    .name(AppUtils.getFullName(user.getFirstName(), user.getLastName()))
                    .account_number(user.getPhone())
                    .type("mobile_money")
                    .currency("GHS")
                    .bank_code("MTN")
                    .build();

            /**
             * headers
             */
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(PAYSTACK_SECRET);

            log.info("Authorization header: {}", headers.getFirst(HttpHeaders.AUTHORIZATION));

            /**
             * request
             */
            HttpEntity entity = new HttpEntity(payload, headers);
            ResponseEntity<CreateRecipientResponse> response = restTemplate.postForEntity(
                PAYSTACK_RECIPIENT_ENDPOINT, entity, CreateRecipientResponse.class
            );

            CreateRecipientResponse responseBody = response.getBody();
            if (responseBody!=null){
                if (Boolean.TRUE.equals(responseBody.getStatus())){
                    user.setRecipientCode(responseBody.getData().getRecipient_code());
                    userRepo.save(user);

                    log.info("Recipient created successfully:->>{}", response.getBody());
                    responseDTO = AppUtils.getResponseDto("Recipient created successfully", HttpStatus.CREATED);
                    return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
                }
            }

            log.error("Unexpected error occurred!");
            responseDTO = AppUtils.getResponseDto("Unexpected error occurred!", HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description Updates an existing booking record identified by ID.
     * @param paymentId the ID of the booking to update.
     * @param acceptPaymentPayload the updated payment data.
     * @return ResponseEntity containing the updated payment record and status info.
     * @author Emmanuel Yidana
     * @createdAt 27th, September 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updatePayment(UUID paymentId, AcceptPaymentPayload acceptPaymentPayload) {
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
     * @createdAt 27th, September 2025
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
     * @createdAt 27th, September 2025
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

                /**
                 * load entity(loan) records
                 */
                Optional<Loan> loanOptional = loanRepo.findById(existingData.getEntityId());
                if (loanOptional.isEmpty()){
                    log.error("Loan record not found:->>{}", existingData.getEntityId());
                    throw new NotFoundException("Loan record not found");
                }
                Loan loan = loanOptional.get();

                /**
                 * update loan status
                 */
                if (loan.getAmountToBorrow() >= existingData.getAmount()){
                    loan.setPaymentStatus(AppConstants.PAID);
                }else if (existingData.getAmount() >= loan.getAmountToBorrow()/2){
                    loan.setPaymentStatus(AppConstants.HALF_PAID);
                }else {
                    loan.setPaymentStatus(AppConstants.PARTIALLY_PAID);
                }
                loanRepo.save(loan);

                return new ResponseEntity<>(HttpStatus.OK);
            }

        }
        return null;
    }
}