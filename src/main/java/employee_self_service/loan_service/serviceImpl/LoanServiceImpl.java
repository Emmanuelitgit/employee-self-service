package employee_self_service.loan_service.serviceImpl;

import employee_self_service.loan_service.dto.LoanPayload;
import employee_self_service.loan_service.models.Loan;
import employee_self_service.loan_service.repo.LoanRepo;
import employee_self_service.loan_service.service.LoanService;
import employee_self_service.notification_service.serviceImpl.NotificationServiceImpl;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.User;
import employee_self_service.user_service.repo.UserRepo;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LoanServiceImpl implements LoanService {

    private final LoanRepo loanRepo;
    private final AppUtils appUtils;
    private final NotificationServiceImpl notificationService;
    private final UserRepo userRepo;

    @Autowired
    public LoanServiceImpl(LoanRepo loanRepo, AppUtils appUtils, NotificationServiceImpl notificationService, UserRepo userRepo) {
        this.loanRepo = loanRepo;
        this.appUtils = appUtils;
        this.notificationService = notificationService;
        this.userRepo = userRepo;
    }

    /**
     * @description This method is used to create a new loan application record
     * @param loanDTO The payload of the loan to be created
     * @return ResponseEntity containing the created loan record and status info
     * @auther Emmanuel Yidana
     * @createdAt 21st August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createLoan(LoanPayload loanDTO) {
        try{
            log.info("In create loan method:->>{}", loanDTO);
            ResponseDTO responseDTO;

            /**
             * retrieve user and manager details
             */
            String username = appUtils.getAuthenticatedUserId(AppUtils.getAuthenticatedUsername());
            log.info("Fetching username from principal:->>{}", username);
            UUID userId = UUID.fromString(username);
            log.info("Fetching user id from principal:->>{}", userId);
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()){
                log.error("User record not exist:->>{}", userId);
                responseDTO = AppUtils.getResponseDto("User record not exist", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            UUID managerId = userOptional.get().getManagerId();
            log.info("Fetching manager id:->>{}", managerId);

            /**
             * validate payload
             */
            log.info("Validating payload....");
            if (loanDTO==null){
                log.error("Loan payload is null");
                responseDTO = AppUtils.getResponseDto("Loan payload cannot null", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            log.info("About to save loan record:->>{}", loanDTO);
            Loan loan = Loan
                    .builder()
                    .loanType(AppConstants.PERSONAL_LOAN)
                    .status(AppConstants.PENDING_MANAGER_APPROVAL)
                    .userId(userId)
                    .expectedPaymentDate(LocalDate.now().plusMonths(1L))
                    .reasonForLoan(loanDTO.getReasonForLoan())
                    .nextOfKin(loanDTO.getNextOfKin())
                    .paymentStatus(AppConstants.PENDING)
                    .managerId(managerId)
                    .bankName(loanDTO.getBankName())
                    .bankBranch(loanDTO.getBankBranch())
                    .bankAccountNumber(loanDTO.getBankAccountNumber())
                    .amountToBorrow(loanDTO.getAmountToBorrow())
                    .amountRemaining(loanDTO.getAmountToBorrow())
                    .build();
            Loan res = loanRepo.save(loan);

            /**
             * return response on success
             */
            log.info("Loan record saved successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Loan record saved successfully", HttpStatus.CREATED, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrieve all loan applications
     * @return ResponseEntity containing the retrieved data and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    @Override
    public ResponseEntity<ResponseDTO> getLoans() {
        try {
            log.info("In fetch all leaves method");
            ResponseDTO responseDTO;

            /**
             * load loans from db
             */
            log.info("About to fetch loans from fb");
            List<Loan> loans = loanRepo.findAll();
            if (loans.isEmpty()){
                log.error("No loan record found");
                responseDTO = AppUtils.getResponseDto("No loan record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Loan fetched successfully");
            responseDTO = AppUtils.getResponseDto("Loan records fetched successfully", HttpStatus.OK, loans);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrieve a specific loan application by id
     * @param loanId The id of the loan record to be retrieved
     * @return ResponseEntity containing the retrieved data and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getLoanById(UUID loanId) {
        try{
            log.info("In get loan record by id:->>{}", loanId);
            ResponseDTO responseDTO;

            /**
             * load loan record from db
             */
            log.info("About to fetch loan record");
            Optional<Loan> loanOptional = loanRepo.findById(loanId);
            if (loanOptional.isEmpty()){
                log.error("Loan record not found:->>{}", loanId);
                responseDTO = AppUtils.getResponseDto("Loan record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Loan record fetched successfully:->>{}", loanOptional.get());
            responseDTO = AppUtils.getResponseDto("Loan record fetched successfully", HttpStatus.OK, loanOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to update a specific loan application by id
     * @param loanId The id of the loan record to be updated
     * @param loan The payload to be updated
     * @return ResponseEntity containing the updated data and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateLoan(LoanPayload loan, UUID loanId) {
        try{
            log.info("In update loan record by id:->>{}", loanId);
            ResponseDTO responseDTO;

            /**
             * load loan record from db
             */
            log.info("About to fetch loan record");
            Optional<Loan> loanOptional = loanRepo.findById(loanId);
            if (loanOptional.isEmpty()){
                log.error("Loan record not found:->>{}", loanId);
                responseDTO = AppUtils.getResponseDto("Loan record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * check if manager has not approved yet
             */
            if (!loanOptional.get().getStatus().equalsIgnoreCase(AppConstants.PENDING_MANAGER_APPROVAL)){
                log.info("Loan record cannot be updated at the moment:->>{}", loanOptional.get().getStatus());
                responseDTO = AppUtils.getResponseDto("Loan cannot be updated at the moment", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            log.info("About to update loan records");
            Loan existingData = loanOptional.get();
            existingData.setLoanType(loan.getLoanType()!=null?loan.getLoanType(): existingData.getLoanType());
            existingData.setAmountToBorrow(loan.getAmountToBorrow()!=null? loan.getAmountToBorrow() : existingData.getAmountToBorrow());
            existingData.setNextOfKin(loan.getNextOfKin()!=null? loan.getNextOfKin() : existingData.getNextOfKin());
            existingData.setReasonForLoan(loan.getReasonForLoan()!=null? loan.getReasonForLoan() : existingData.getReasonForLoan());
            existingData.setBankName(loan.getBankName()!=null? loan.getBankName() : existingData.getBankName());
            existingData.setBankBranch(loan.getBankBranch()!=null? loan.getBankBranch() : existingData.getBankBranch());
            existingData.setBankAccountNumber(loan.getBankAccountNumber()!=null? loan.getBankAccountNumber() : existingData.getBankAccountNumber());
            existingData.setAmountRemaining(existingData.getAmountToBorrow());
            /**
             * remarks reserved for only accountant
             */
            if (appUtils.getAuthenticatedUserRole().equalsIgnoreCase(AppConstants.ACCOUNTANT_ROLE)){
                if (loan.getRemarks()!=null){
                    existingData.setRemarks(loan.getRemarks());
                }
            }
            Loan res = loanRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Loan record updated successfully:->>{}", loanOptional.get());
            responseDTO = AppUtils.getResponseDto("Loan record fetched successfully", HttpStatus.OK, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to delete a specific loan application by id
     * @param loanId The id of the loan record to be deleted
     * @return ResponseEntity containing message and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> removeLoan(UUID loanId) {
        try {
            log.info("In delete loan record method:->>{}", loanId);
            ResponseDTO responseDTO;

            /**
             * check if leave record exist
             */
            log.info("About to load loan record from db");
            Optional<Loan> loanOptional = loanRepo.findById(loanId);
            if (loanOptional.isEmpty()){
                log.error("Loan record not found:->>{}", loanId);
                responseDTO = AppUtils.getResponseDto("Loan record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * check if manager has not approved yet
             */
            if (!loanOptional.get().getStatus().equalsIgnoreCase(AppConstants.PENDING_MANAGER_APPROVAL)){
                log.info("Loan record cannot be removed at the moment:->>{}", loanOptional.get().getStatus());
                responseDTO = AppUtils.getResponseDto("Loan cannot be removed at the moment", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
            log.info("About to delete leave record");
            loanRepo.deleteById(loanId);

            /**
             * return response on success
             */
            log.info("Leave record deleted successfully");
            responseDTO = AppUtils.getResponseDto("Leave record deleted successfully", HttpStatus.OK, loanOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to approve/reject a specific loan application by id
     * @param loanId The id of the loan record to be approved/rejected
     * @return ResponseEntity containing the approval info data and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @PreAuthorize("hasAnyAuthority('ACCOUNTANT','HR','MANAGER')")
    @Override
    public ResponseEntity<ResponseDTO> approveOrRejectLoan(UUID loanId, String status) {
        try{
            log.info("In loan approval flow method:->>{}", status);
            ResponseDTO responseDTO;

            /**
             * check if loan record exist
             */
            log.info("About to load loan record from db");
            Optional<Loan> loanOptional = loanRepo.findById(loanId);
            if (loanOptional.isEmpty()){
                log.error("Loan record not found:->>{}", loanId);
                responseDTO = AppUtils.getResponseDto("Loan record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            Loan existingData = loanOptional.get();
            if (AppConstants.PENDING_HR_APPROVAL.equalsIgnoreCase(status)){
                log.info("Loan status changed from {} to {}", loanOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.PENDING_HR_APPROVAL);

            }else if (AppConstants.PENDING_ACCOUNTANT_APPROVAL.equalsIgnoreCase(status)){
                log.info("Loan status changed from {} to {}", loanOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.PENDING_ACCOUNTANT_APPROVAL);
                existingData.setStatus(AppConstants.PROCESSING);

            }else if (AppConstants.APPROVED.equalsIgnoreCase(status)) {
                log.info("Loan status changed from {} to {}", loanOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.APPROVED);

            } else if (AppConstants.REJECTED.equalsIgnoreCase(status)) {
                log.info("Loan status changed from {} to {}", loanOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.REJECTED);

            }else {
                log.info("Status does not exist");
            }
            Loan res = loanRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Loan status updated successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Loan status updated successfully", HttpStatus.OK, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * @description This method is used to cancel a specific loan application by id
     * @param loanId The id of the loan record to be cancelled
     * @return ResponseEntity containing the cancelled data and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> cancelLoan(UUID loanId) {
        try {
            log.info("In cancel loan method:->>{}", loanId);
            ResponseDTO responseDTO;

            /**
             * check if leave record exist
             */
            log.info("About to load loan record from db");
            Optional<Loan> loanOptional = loanRepo.findById(loanId);
            if (loanOptional.isEmpty()){
                log.error("Loan record not found:->>{}", loanId);
                responseDTO = AppUtils.getResponseDto("Loan record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * check if manager has not yet approved
             */
            if (!AppConstants.PENDING_MANAGER_APPROVAL.equalsIgnoreCase(loanOptional.get().getStatus())){
                log.info("Manager has approved already and cancellation cannot be done the moment");
                responseDTO = AppUtils.getResponseDto("Manager has approved already and cancellation cannot be done the moment",
                        HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            log.warn("About to cancel leave application");
            Loan existingData = loanOptional.get();
            existingData.setStatus(AppConstants.CANCELLED);
            Loan res = loanRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Loan application cancelled successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Loan application cancelled successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to fetch loans for logged-in user
     * @return ResponseEntity containing the returned loans and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> fetchLoansForLoggedInUser() {
        try {
            log.info("In fetch loans for logged-in user method");
            ResponseDTO responseDTO;

            String username = appUtils.getAuthenticatedUserId(AppUtils.getAuthenticatedUsername());
            log.info("Fetching username from principal:->>{}", username);
            UUID userId = UUID.fromString(username);
            log.info("Fetching user id from principal:->>{}", userId);

            /**
             * load loans from db by user id
             */
            log.info("About to load user loans from db");
            List<Loan> loans = loanRepo.fetchLoansForLoggedInUser(userId);
            if (loans.isEmpty()){
                log.error("No loan record found for user:->>{}", userId);
                responseDTO = AppUtils.getResponseDto("No loan record found for user", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.info("Loans fetched successfully:->>{}", loans);
            responseDTO = AppUtils.getResponseDto("Loans fetched successfully", HttpStatus.OK, loans);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to fetch loans for logged-in Manager/HR
     * This only filter out loans pending for their approval
     * @return ResponseEntity containing the returned loans and status info
     * @auther Emmanuel Yidana
     * @createdAt 22nd August 2025
     */
    @PreAuthorize("hasAnyAuthority('MANAGER','ACCOUNTANT','HR')")
    @Override
    public ResponseEntity<ResponseDTO> fetchLoansForManagerAndHR() {
        try {
            log.info("In fetch loans for manager and HR method");
            ResponseDTO responseDTO;

            String username = appUtils.getAuthenticatedUserId(AppUtils.getAuthenticatedUsername());
            log.info("Fetching username from principal:->>{}", username);
            UUID userId = UUID.fromString(username);
            log.info("Fetching user id from principal:->>{}", userId);

            /**
             * load loan from db by user id and base on role
             */
            List<Loan> loans = new ArrayList<>();
            if (AppConstants.MANAGER_ROLE.equalsIgnoreCase(appUtils.getAuthenticatedUserRole())){
                log.info("About to load loans for manager");
                List<Loan> loansFromDB = loanRepo.fetchLoansForManager(userId);
                loans.addAll(loansFromDB );

            } else if (AppConstants.HR_ROLE.equalsIgnoreCase(appUtils.getAuthenticatedUserRole())) {
                log.info("About to load loans for HR");
                List<UUID> hrCompaniesIDs = userRepo.getHRCompaniesIds(userId);
                log.info("Fetching HR companies Ids:->>{}", hrCompaniesIDs);
                List<Loan> loansFromDB  = loanRepo.fetchLoansForHR(hrCompaniesIDs);
                loans.addAll(loansFromDB);

            }else if (AppConstants.ACCOUNTANT_ROLE.equalsIgnoreCase(appUtils.getAuthenticatedUserRole())) {
                log.info("About to load loans for Accountant");
                List<UUID> accountantCompaniesIds = userRepo.getAccountantCompaniesIds(userId);
                log.info("Fetching Accountant companies Ids:->>{}", accountantCompaniesIds);
                List<Loan> loansFromDB  = loanRepo.fetchLoansForAccountant(accountantCompaniesIds);
                loans.addAll(loansFromDB);

            }else {
                log.error("User not authorized to this feature");
                responseDTO = AppUtils.getResponseDto("User not authorized to this feature", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * check if retrieved data is null/empty
             */
            if (loans.isEmpty()){
                log.error("No loan record found for user:->>{}", userId);
                responseDTO = AppUtils.getResponseDto("No loan record found for user", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.info("Loans fetched successfully:->>{}", loans);
            responseDTO = AppUtils.getResponseDto("Loans fetched successfully", HttpStatus.OK, loans);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}