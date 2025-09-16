package employee_self_service.user_service.service;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Company;
import employee_self_service.user_service.models.Department;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface CompanyService {
    ResponseEntity<ResponseDTO> createCompany(Company company);
    ResponseEntity<ResponseDTO> getCompanies();
    ResponseEntity<ResponseDTO> getCompanyById(UUID companyId);
    ResponseEntity<ResponseDTO> updateCompany(Company company, UUID companyId);
    ResponseEntity<ResponseDTO> removeCompany(UUID companyId);
}
