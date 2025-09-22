package employee_self_service.loan_service.rest;

import employee_self_service.loan_service.dto.LoanPayload;
import employee_self_service.loan_service.repo.LoanRepo;
import employee_self_service.loan_service.serviceImpl.LoanServiceImpl;
import employee_self_service.user_service.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Loan Management")
@RestController
@RequestMapping("/api/v1/loans")
public class LoanRest {

    private final LoanServiceImpl loanService;

    @Autowired
    public LoanRest(LoanServiceImpl loanService) {
        this.loanService = loanService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createLoan(LoanPayload loanDTO){
        return loanService.createLoan(loanDTO);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getLoans(){
        return loanService.getLoans();
    }

    @GetMapping("/{loanId}")
    public ResponseEntity<ResponseDTO> getLoanById(@PathVariable UUID loanId){
        return loanService.getLoanById(loanId);
    }

    @PutMapping("/approval/{loanId}")
    public ResponseEntity<ResponseDTO> approveOrRejectLoan(@PathVariable UUID loanId,
                                                           @RequestParam(name = "status") String status){
        return loanService.approveOrRejectLoan(loanId, status);
    }

    @PutMapping("cancel/{loanId}")
    public ResponseEntity<ResponseDTO> cancelLoan(@PathVariable UUID loanId){
        return loanService.cancelLoan(loanId);
    }

    @GetMapping("/for-logged-in-user")
    public ResponseEntity<ResponseDTO> fetchLoansForLoggedInUser(){
        return loanService.fetchLoansForLoggedInUser();
    }

    @GetMapping("/for-hr-manager")
    public ResponseEntity<ResponseDTO> fetchLoansForManagerAndHR(){
        return loanService.fetchLoansForManagerAndHR();
    }
}
