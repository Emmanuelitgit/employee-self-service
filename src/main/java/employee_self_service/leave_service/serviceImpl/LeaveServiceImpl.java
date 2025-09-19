package employee_self_service.leave_service.serviceImpl;

import employee_self_service.leave_service.models.Leave;
import employee_self_service.leave_service.models.UserLeaveBalance;
import employee_self_service.leave_service.repo.LeaveRepo;
import employee_self_service.leave_service.repo.UserLeaveBalanceRepo;
import employee_self_service.leave_service.service.LeaveService;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.exception.NotFoundException;
import employee_self_service.util.AppConstants;
import employee_self_service.util.AppUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepo leaveRepo;
    private final AppUtils appUtils;
    private final UserLeaveBalanceRepo userLeaveBalanceRepo;

    @Autowired
    public LeaveServiceImpl(LeaveRepo leaveRepo, AppUtils appUtils, UserLeaveBalanceRepo userLeaveBalanceRepo) {
        this.leaveRepo = leaveRepo;
        this.appUtils = appUtils;
        this.userLeaveBalanceRepo = userLeaveBalanceRepo;
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
            leave.setStatus(AppConstants.PENDING_MANAGER_APPROVAL);
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
        try{
            log.info("In approval flow method:->>{}", status);
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
            Leave existingData = leaveOptional.get();
            if (AppConstants.PENDING_HR_APPROVAL.equalsIgnoreCase(status)){
                log.info("Leave status changed from {} to {}", leaveOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.PENDING_HR_APPROVAL);
            } else if (AppConstants.APPROVED.equalsIgnoreCase(status)) {
                log.info("Leave status changed from {} to {}", leaveOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.APPROVED);
                /**
                 * update leave balance only when type is annual leave
                 * no deductions for sick and maternal leaves
                 */
                if (AppConstants.ANNUAL_LEAVE.equalsIgnoreCase(existingData.getLeaveType())){
                    updateLeaveBalance(existingData.getUserId(), existingData.getLeaveDays());
                }
            } else if (AppConstants.REJECTED.equalsIgnoreCase(status)) {
                log.info("Leave status changed from {} to {}", leaveOptional.get().getStatus(), status);
                existingData.setStatus(AppConstants.REJECTED);
            }else {
                log.info("Status does not exist");
            }
            Leave res = leaveRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Leave status updated successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Leave status updated successfully", HttpStatus.OK, res);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to fetch leaves for logged-in user
     * @return ResponseEntity containing the returned leaves and status info
     */
    @Override
    public ResponseEntity<ResponseDTO> fetchLeavesForLoggedInUser() {
        try {
            log.info("In fetch leaves for logged-in user method");
            ResponseDTO responseDTO;

            /**
             * load leaves from db by user id
             */
            log.info("About to load user leaves from db");
            UUID userId = UUID.fromString(appUtils.getAuthenticatedUserId());
            log.info("Fetching user id from principal:->>{}", userId);
            List<Leave> leaves = leaveRepo.fetchLeavesForLoggedInUser(userId);
            if (leaves.isEmpty()){
                log.error("No leave record found for user:->>{}", userId);
                responseDTO = AppUtils.getResponseDto("No leave record found for user", HttpStatus.NOT_FOUND);
                return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
            }

            /**
             * return response on success
             */
            log.info("Leaves fetched successfully:->>{}", leaves);
            responseDTO = AppUtils.getResponseDto("Leaves fetched successfully", HttpStatus.OK, leaves);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description This method is used to fetch leaves for logged-in Manager/HR
     * This only filter out leaves pending for their approval
     * @return ResponseEntity containing the returned leaves and status info
     */
    @Override
    public ResponseEntity<ResponseDTO> fetchLeavesForManagerAndHR() {
        try {
            log.info("In fetch leaves for manager and HR method");
            ResponseDTO responseDTO;

            UUID userId = UUID.fromString(appUtils.getAuthenticatedUserId());
            log.info("Fetching user id from principal:->>{}", userId);

            /**
             * load leaves from db by user id and base on role
             */
            List<Leave> leaves = new ArrayList<>();
            if (AppConstants.MANAGER_ROLE.equalsIgnoreCase(appUtils.getAuthenticatedUserRole())){
                log.info("About to load leaves for manager");
                List<Leave> leavesFromDB = leaveRepo.fetchLeavesForManager(userId);
                leaves.addAll(leavesFromDB);
                if (leaves.isEmpty()){
                    log.error("No leave record found for user:->>{}", userId);
                    responseDTO = AppUtils.getResponseDto("No leave record found for user", HttpStatus.NOT_FOUND);
                    return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
                }

            } else if (AppConstants.HR_ROLE.equalsIgnoreCase(appUtils.getAuthenticatedUserRole())) {
                log.info("About to load leaves for HR");
                List<Leave> leavesFromDB = leaveRepo.fetchLeavesForHR(userId);
                leaves.addAll(leavesFromDB);
                if (leaves.isEmpty()){
                    log.error("No leave record found for user:->>{}", userId);
                    responseDTO = AppUtils.getResponseDto("No leave record found for user", HttpStatus.NOT_FOUND);
                    return new ResponseEntity<>(responseDTO, HttpStatus.NOT_FOUND);
                }

            }else {
                log.error("User not authorized to this feature");
                responseDTO = AppUtils.getResponseDto("User not authorized to this feature", HttpStatus.UNAUTHORIZED);
                return new ResponseEntity<>(responseDTO, HttpStatus.UNAUTHORIZED);
            }

            /**
             * return response on success
             */
            log.info("Leaves fetched successfully:->>{}", leaves);
            responseDTO = AppUtils.getResponseDto("Leaves fetched successfully", HttpStatus.OK, leaves);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
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
        try {
            log.info("In cancel leave method:->>{}", leaveId);
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
            log.warn("About to cancel leave application");
            Leave existingData = leaveOptional.get();
            existingData.setStatus(AppConstants.CANCELLED);
            Leave res = leaveRepo.save(existingData);

            /**
             * return response on success
             */
            log.info("Leave application cancelled successfully:->>{}", res);
            responseDTO = AppUtils.getResponseDto("Leave status updated successfully", HttpStatus.OK);
            return new ResponseEntity<>(responseDTO, HttpStatus.OK);

        }catch (Exception e) {
            log.error("Exception Occurred!, statusCode -> {} and Cause -> {} and Message -> {}", 500, e.getCause(), e.getMessage());
            ResponseDTO  response = AppUtils.getResponseDto(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @description a helper method used to update user leave balance on leave approval
     * @param userId The id of user the leave belongs to
     * @param leaveDays The new balance to be updated with
     */
    private void updateLeaveBalance(UUID userId, Long leaveDays){
        Optional<UserLeaveBalance> leaveBalanceOptional = userLeaveBalanceRepo.findByUsrId(userId);
        if (leaveBalanceOptional.isEmpty()){
            log.error("User leave balance record not found:->>{}", userId);
            throw new NotFoundException("User leave balance record not found");
        }
        UserLeaveBalance existingData = leaveBalanceOptional.get();
        float newBalance = existingData.getLeaveBalance()-leaveDays;
        existingData.setLeaveBalance(newBalance);
        userLeaveBalanceRepo.save(existingData);
    }

    /**
     * A chron job method runs at every 6pm to calculate/update leave balances of all employees
     */
    @Scheduled(cron = "0 0 18 * * 1-5")
    private void calculateLeaveBalanceDaily(){
        log.info("About to run chron job");
        float balance =  (((float) 15 /365)*1);
        float roundedValue  = AppUtils.roundNumber(balance, 2);
        log.info("About to update leave balance for the day");
        userLeaveBalanceRepo.updateLeaveBalance(roundedValue);
        log.info("Leave balances updated successfully for:->>{}", LocalDateTime.now());
    }
}