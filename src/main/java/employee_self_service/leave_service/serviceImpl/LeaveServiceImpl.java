package employee_self_service.leave_service.serviceImpl;

import employee_self_service.leave_service.models.Leave;
import employee_self_service.leave_service.repo.LeaveRepo;
import employee_self_service.leave_service.service.LeaveService;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepo leaveRepo;
    private final AppUtils appUtils;

    @Autowired
    public LeaveServiceImpl(LeaveRepo leaveRepo, AppUtils appUtils) {
        this.leaveRepo = leaveRepo;
        this.appUtils = appUtils;
    }

    /**
     * @description This method is used to create a new leave application record
     * @param leave The payload of the leave to be created
     * @return ResponseEntity containing the created leave record and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> createLeave(Leave leave) {
        try{
            log.info("In create leave method:->>{}", leave);
            ResponseDTO responseDTO;

            /**
             * validate payload
             */
            log.info("Validating payload....");
            if (leave==null){
                log.error("Leave payload is null");
                responseDTO = AppUtils.getResponseDto("Leave payload cannot null", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }
            log.info("About to save leave record:->>{}", leave);
            UUID userId = UUID.fromString(appUtils.getAuthenticatedUserId());
            Long leaveCount = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
            leave.setUserId(userId);
            leave.setLeaveDays(leaveCount);
            leave.setStatus(AppConstants.PENDING);
            Leave res = leaveRepo.save(leave);

            /**
             * return response on success
             */
            log.info("Leave record saved successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Leave record saved successfully", HttpStatus.CREATED, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrieve all leave applications
     * @return ResponseEntity containing the retrieved data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getLeaves() {
        try {
            log.info("In fetch all leaves method");
            ResponseDTO responseDTO;

            /**
             * load leaves from db
             */
            log.info("About to fetch leaves from fb");
            List<Leave> leaves = leaveRepo.findAll();
            if (leaves.isEmpty()){
                log.error("No leave record found");
                responseDTO = AppUtils.getResponseDto("No leave record found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Leaves fetched successfully");
            responseDTO = AppUtils.getResponseDto("Leave records fetched successfully", HttpStatus.OK, leaves);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to retrieve a specific leave application by id
     * @param leaveId The id of the leave record to be retrieved
     * @return ResponseEntity containing the retrieved data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> getLeaveById(UUID leaveId) {
        try{
            log.info("In get leave record by id:->>{}", leaveId);
            ResponseDTO responseDTO;

            /**
             * load leave record from db
             */
            log.info("About to fetch leave record");
            Optional<Leave> leaveOptional = leaveRepo.findById(leaveId);
            if (leaveOptional.isEmpty()){
                log.error("Leave record not found:->>{}", leaveId);
                responseDTO = AppUtils.getResponseDto("Leave record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            /**
             * return response on success
             */
            log.info("Leave record fetched successfully:->>{}", leaveOptional.get());
            responseDTO = AppUtils.getResponseDto("Leave record fetched successfully", HttpStatus.OK, leaveOptional.get());
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to update a specific leave application by id
     * @param leaveId The id of the leave record to be updated
     * @param leave The payload to be updated
     * @return ResponseEntity containing the updated data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> updateLeave(Leave leave, UUID leaveId) {
        try {
            log.info("In update leave method:->>{}", leave);
            ResponseDTO responseDTO;

            /**
             * check if leave record exist
             */
            log.info("About to load existing leave record from db");
            Optional<Leave> leaveOptional = leaveRepo.findById(leaveId);
            if (leaveOptional.isEmpty()){
                log.error("Leave record not found:->>{}", leaveId);
                responseDTO = AppUtils.getResponseDto("Leave record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * check if leave has already been approved either by Manager or HR
             */
            if (
                    AppConstants.PENDING_HR_APPROVAL.equalsIgnoreCase(leaveOptional.get().getStatus()) ||
                    AppConstants.APPROVED.equalsIgnoreCase(leaveOptional.get().getStatus())
            ){
                log.error("Leave applications cannot be updated at the moment");
                responseDTO = AppUtils.getResponseDto("Leave applications cannot be updated at the moment", HttpStatus.BAD_REQUEST);
                return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
            }

            log.info("About to update leave record");
            Leave existingData = leaveOptional.get();
            existingData.setStartDate(leave.getStartDate()!=null?leave.getStartDate():existingData.getStartDate());
            existingData.setEndDate(leave.getEndDate()!=null?leave.getEndDate():existingData.getEndDate());
            existingData.setLeaveType(leave.getLeaveType()!=null?leave.getLeaveType():existingData.getLeaveType());
            if (leave.getStartDate()!=null&&leave.getEndDate()!=null){
                Long leaveCount = ChronoUnit.DAYS.between(leave.getStartDate(), leave.getEndDate());
                existingData.setLeaveDays(leaveCount);
            }
            log.info("About to save updated record");
            Leave res = leaveRepo.save(leave);

            /**
             * return response on success
             */
            log.info("Leave record updated successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Leave record updated successfully", HttpStatus.OK, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to delete a specific leave application by id
     * @param leaveId The id of the leave record to be deleted
     * @return ResponseEntity containing the deleted data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> removeLeave(UUID leaveId) {
        try {
            log.info("In delete leave record method:->>{}", leaveId);
            ResponseDTO responseDTO;

            /**
             * check if leave record exist
             */
            log.info("About to load leave record from db");
            Optional<Leave> leaveOptional = leaveRepo.findById(leaveId);
            if (leaveOptional.isEmpty()){
                log.error("Leave record not found:->>{}", leaveId);
                responseDTO = AppUtils.getResponseDto("Leave record not found", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }
            log.info("About to delete leave record");
            leaveRepo.deleteById(leaveId);

            /**
             * return response on success
             */
            log.info("Leave record deleted successfully");
            responseDTO = AppUtils.getResponseDto("Leave record deleted successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to approve/reject a specific leave application by id
     * @param leaveId The id of the leave record to be approved/rejected
     * @return ResponseEntity containing the approval info data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> approveOrRejectLeave(UUID leaveId, String status) {
        return null;
    }

    /**
     * @description This method is used to cancel a specific leave application by id
     * @param leaveId The id of the leave record to be cancelled
     * @return ResponseEntity containing the cancelled data and status info
     * @auther Emmanuel Yidana
     * @createdAt 18th August 2025
     */
    @Override
    public ResponseEntity<ResponseDTO> cancelLeave(UUID leaveId) {
        return null;
    }
}
