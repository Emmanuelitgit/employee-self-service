package employee_self_service.user_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_role_tbl")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRole extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID userId;
    private UUID roleId;
}
