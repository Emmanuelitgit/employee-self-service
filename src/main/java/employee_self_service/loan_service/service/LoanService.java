package employee_self_service.loan_service.service;

import employee_self_service.loan_service.dto.LoanPayload;
import employee_self_service.loan_service.models.Loan;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface LoanService {
    ResponseEntity<ResponseDTO> createLoan(LoanPayload loan);
    ResponseEntity<ResponseDTO> getLoans();
    ResponseEntity<ResponseDTO> getLoanById(UUID loanId);
    ResponseEntity<ResponseDTO> updateLoan(LoanPayload loan, UUID loanId);
    ResponseEntity<ResponseDTO> removeLoan(UUID loanId);
    ResponseEntity<ResponseDTO> approveOrRejectLoan(UUID loanId, String status);
    ResponseEntity<ResponseDTO> cancelLoan(UUID loanId);
    ResponseEntity<ResponseDTO> fetchLoansForLoggedInUser();
    ResponseEntity<ResponseDTO> fetchLoansForManagerAndHR();
}
