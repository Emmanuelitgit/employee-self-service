package employee_self_service.user_service.service;
import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.dto.UserPayloadDTO;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface UserService {

    ResponseEntity<ResponseDTO> createUser(UserPayloadDTO userPayloadDTO);
    ResponseEntity<ResponseDTO> getUsers();
    ResponseEntity<ResponseDTO> getUserById(UUID userId);
    ResponseEntity<ResponseDTO> updateUser(UserPayloadDTO user, UUID userId);
    ResponseEntity<ResponseDTO> removeUser(UUID userId);
}
