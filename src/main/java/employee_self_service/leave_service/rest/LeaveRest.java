package employee_self_service.leave_service.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Leave Management")
@RestController
@RequestMapping("/api/v1/leaves")
public class LeaveRest {
}
