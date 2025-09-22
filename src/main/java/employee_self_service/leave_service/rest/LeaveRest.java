package employee_self_service.leave_service.rest;

import employee_self_service.leave_service.dto.LeavePayload;
import employee_self_service.leave_service.models.Leave;
import employee_self_service.leave_service.serviceImpl.LeaveServiceImpl;
import employee_self_service.user_service.dto.ResponseDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Tag(name = "Leave Management")
@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveRest {
    private final LeaveServiceImpl leaveService;

    @Autowired
    public LeaveRest(LeaveServiceImpl leaveService) {
        this.leaveService = leaveService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createLeave(@RequestBody LeavePayload leave){
        return leaveService.createLeave(leave);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getLeaves(){
        return leaveService.getLeaves();
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getLeaveById(UUID leaveId){
        return leaveService.getLeaveById(leaveId);
    }

    @PutMapping("/{leaveId}")
    public ResponseEntity<ResponseDTO> updateLeave(@RequestBody LeavePayload leave, @PathVariable UUID leaveId){
        return leaveService.updateLeave(leave, leaveId);
    }

    @DeleteMapping
    public ResponseEntity<ResponseDTO> removeLeave(UUID leaveId){
        return leaveService.removeLeave(leaveId);
    }

    @PutMapping("/approve-reject/{leaveId}")
    public ResponseEntity<ResponseDTO> approveOrRejectLeave(@PathVariable UUID leaveId,
                                                            @RequestParam(name = "status") String status){
        return leaveService.approveOrRejectLeave(leaveId, status);
    }

    @GetMapping("/for-logged-in-user")
    public ResponseEntity<ResponseDTO> fetchLeavesForLoggedInUser(){
        return leaveService.fetchLeavesForLoggedInUser();
    }

    @GetMapping("/for-hr-manager")
    public ResponseEntity<ResponseDTO> fetchLeavesForManagerAndHR(){
        return leaveService.fetchLeavesForManagerAndHR();
    }

    @GetMapping("/cancel/{leaveId}")
    public ResponseEntity<ResponseDTO> cancelLeave(@PathVariable UUID leaveId){
        return leaveService.cancelLeave(leaveId);
    }
}
