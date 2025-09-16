package employee_self_service.user_service.rest;

import employee_self_service.user_service.dto.ResponseDTO;
import employee_self_service.user_service.dto.UserPayloadDTO;
import employee_self_service.user_service.serviceImpl.UserServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserRest {

    private final UserServiceImpl userService;

    @Autowired
    public UserRest(UserServiceImpl userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<ResponseDTO> createUser(@RequestBody @Valid UserPayloadDTO user){
        return userService.createUser(user);
    }

    @GetMapping
    public ResponseEntity<ResponseDTO> getUsers(){
        return userService.getUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ResponseDTO> getUserById(@PathVariable UUID userId){
        return userService.getUserById(userId);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ResponseDTO> updateUser(@RequestBody @Valid UserPayloadDTO user, @PathVariable UUID userId){
        return userService.updateUser(user, userId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<ResponseDTO> removeUser(@PathVariable UUID userId){
        return userService.removeUser(userId);
    }
}
