package employee_self_service.user_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "user_department_tbl", schema = "user_schema")
public class UserDepartment extends AuditorData {
    @jakarta.persistence.Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    @Column(name = "user_id", nullable = false)
    private UUID userId;
    @Column(name ="department_id" , nullable = false)
    private UUID departmentId;
}
