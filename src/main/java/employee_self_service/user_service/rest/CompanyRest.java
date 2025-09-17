package employee_self_service.user_service.rest;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Company;
import employee_self_service.user_service.repo.CompanyRepo;
import employee_self_service.user_service.serviceImpl.CompanyServiceImpl;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Company Management")
@RestController
@RequestMapping("/api/v1/companies")
public class CompanyRest {

    private final CompanyServiceImpl companyService;

    @Autowired
    public CompanyRest(CompanyServiceImpl companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createCompany(@RequestBody Company company){
        return companyService.createCompany(company);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getCompanies(){
        return companyService.getCompanies();
    }

    @GetMapping("/{companyId)}")
    public ResponseEntity<ResponseDTO> getCompanyById(@PathVariable UUID companyId){
        return companyService.getCompanyById(companyId);
    }

    @PutMapping("/{companyId}")
    public ResponseEntity<ResponseDTO> updateCompany(@RequestBody Company company, @PathVariable UUID companyId){
        return companyService.updateCompany(company, companyId);
    }

    @DeleteMapping("/{companyId}")
    public ResponseEntity<ResponseDTO> removeCompany(@PathVariable UUID companyId){
        return companyService.removeCompany(companyId);
    }
}
