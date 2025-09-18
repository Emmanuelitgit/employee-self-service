package employee_self_service.leave_service.service;

import employee_self_service.leave_service.models.Leave;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface LeaveService {
    ResponseEntity<ResponseDTO> createLeave(Leave leave);
    ResponseEntity<ResponseDTO> getLeaves();
    ResponseEntity<ResponseDTO> getLeaveById(UUID leaveId);
    ResponseEntity<ResponseDTO> updateLeave(Leave leave, UUID leaveId);
    ResponseEntity<ResponseDTO> removeLeave(UUID leaveId);
    ResponseEntity<ResponseDTO> approveOrRejectLeave(UUID leaveId, String status);
    ResponseEntity<ResponseDTO> cancelLeave(UUID leaveId);
}