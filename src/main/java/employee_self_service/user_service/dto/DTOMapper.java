package employee_self_service.user_service.dto;

import employee_self_service.user_service.models.User;
import org.springframework.stereotype.Component;

@Component
public class DTOMapper {

    /* this method takes user object and transform it to userDTO*/
    public static UserDTO toUserDTO(User user, String role){
       return UserDTO
                .builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(role)
                .phone(user.getPhone())
                .username(user.getUsername())
                .build();
    }


    public User toUserEntity(UserPayloadDTO user){
        return User
                .builder()
                .email(user.getEmail())
                .password(user.getPassword())
                .phone(user.getPhone())
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .build();
    }

}
