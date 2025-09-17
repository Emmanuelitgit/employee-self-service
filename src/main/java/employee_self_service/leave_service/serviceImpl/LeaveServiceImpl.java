package employee_self_service.leave_service.serviceImpl;

import employee_self_service.leave_service.models.Leave;
import employee_self_service.leave_service.repo.LeaveRepo;
import employee_self_service.leave_service.service.LeaveService;
import employee_self_service.user_service.dto.ResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LeaveServiceImpl implements LeaveService {
    private final LeaveRepo leaveRepo;

    public LeaveServiceImpl(LeaveRepo leaveRepo) {
        this.leaveRepo = leaveRepo;
    }

    @Override
    public ResponseEntity<ResponseDTO> createLeave(Leave leave) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> getLeaves() {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> getLeaveById(UUID leaveId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> updateLeave(Leave leave, UUID leaveId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> removeLeave(UUID leaveId) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> approveOrRejectLeave(UUID leaveId, String status) {
        return null;
    }

    @Override
    public ResponseEntity<ResponseDTO> cancelLeave(UUID leaveId) {
        return null;
    }
}
