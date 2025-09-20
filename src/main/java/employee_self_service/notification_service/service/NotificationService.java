package employee_self_service.notification_service.service;

import employee_self_service.notification_service.dto.LeaveDTO;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationService {
    void alertUser(LeaveDTO confirmationDTO);
    void alertManager(LeaveDTO confirmationDTO);
    void alertHR(LeaveDTO confirmationDTO);
}
