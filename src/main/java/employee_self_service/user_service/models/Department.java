package employee_self_service.user_service.models;

import employee_self_service.config.AuditorData;
import jakarta.persistence.*;
import lombok.*;
import org.checkerframework.checker.guieffect.qual.UI;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "department_setup_tbl", schema = "user_schema")
public class Department extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID Id;
    private String name;
    private UUID managerId;
}
