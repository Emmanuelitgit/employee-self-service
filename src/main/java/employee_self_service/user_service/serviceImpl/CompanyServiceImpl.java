package employee_self_service.user_service.serviceImpl;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.models.Company;
import employee_self_service.user_service.repo.CompanyRepo;
import employee_self_service.user_service.service.CompanyService;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepo companyRepo;

    public CompanyServiceImpl(CompanyRepo companyRepo) {
        this.companyRepo = companyRepo;
    }

    @Override
    public ResponseEntity<ResponseDTO> createCompany(Company company) {
        try {
            log.info("In create company method:->>>{}", company);
            ResponseDTO responseDTO;
            /**
             * validate payload
             */
            if (company==null){
                responseDTO = AppUtils.getResponseDto("Company payload cannot be null", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
            /**
             * save and return response
             */
            Company companyRes = companyRepo.save(company);
            responseDTO = AppUtils.getResponseDto("Company added", HttpStatus.CREATED, companyRes);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> getCompanies() {
       try {
           log.info("In get all companies method");
           ResponseDTO responseDTO;
           /**
            * loading data from db
            */
           List<Company> companies = companyRepo.findAll();
           if (companies.isEmpty()){
               responseDTO = AppUtils.getResponseDto("No company record found", HttpStatus.NOT_FOUND);
               return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
           }

           /**
            * return response on success
            */
           responseDTO = AppUtils.getResponseDto("Companies", HttpStatus.OK, companies);
           return new ResponseEntity<>(responseDTO, HttpStatus.OK);

       }catch (Exception e) {
           log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
           ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
           return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
       }
    }

    @Override
    public ResponseEntity<ResponseDTO> getCompanyById(UUID companyId) {
        try {
            log.info("In get company by id method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            Optional<Company> companyOptional = companyRepo.findById(companyId);
            if (companyOptional.isEmpty()){
                log.error("Company record not found:->>>{}", companyId);
                responseDTO = AppUtils.getResponseDto("Company record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            responseDTO = AppUtils.getResponseDto("Company record", HttpStatus.OK, companyOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> updateCompany(Company company, UUID companyId) {
        try {
            log.info("In get company by id method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            Optional<Company> companyOptional = companyRepo.findById(companyId);
            if (companyOptional.isEmpty()){
                log.error("Company record not found:->>>{}", companyId);
                responseDTO = AppUtils.getResponseDto("Company record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * save updated record
             */
            Company existingCompany = companyOptional.get();
            existingCompany.setName(company.getName()!=null?company.getName(): existingCompany.getName());
            Company companyRes = companyRepo.save(company);

            /**
             * return response on success
             */
            log.info("Company updated successfully:->>{}", companyRes);
            responseDTO = AppUtils.getResponseDto("Company record", HttpStatus.OK, companyRes);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ResponseDTO> removeCompany(UUID companyId) {
        try {
            log.info("In get company by id method");
            ResponseDTO responseDTO;
            /**
             * loading data from db
             */
            Optional<Company> companyOptional = companyRepo.findById(companyId);
            if (companyOptional.isEmpty()){
                log.error("Company record not found:->>>{}", companyId);
                responseDTO = AppUtils.getResponseDto("Company record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * delete record
             */
            companyRepo.deleteById(companyId);
            /**
             * return response on success
             */
            log.info("Company deleted successfully:");
            responseDTO = AppUtils.getResponseDto("Company record deleted", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
