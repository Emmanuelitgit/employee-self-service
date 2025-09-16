package employee_self_service.user_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user_tbl", schema = "user_schema")
public class User extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String username;
    private String password;
    private UUID departmentId;
    private UUID companyId;
    private UUID managerId;
}
