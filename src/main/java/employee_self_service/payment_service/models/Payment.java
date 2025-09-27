package employee_self_service.payment_service.models;

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
@Table(name = "payment_tb", schema = "payment_schema")
public class Payment extends AuditorData {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private UUID bookingId;
    private Float amount;
    private Long transactionId;
    private String paymentStatus;
    private String access_code;
    private String reference;
    private String currency;
    private String channel;
    private String paymentDate;
    private String paymentType; // loan, salary;
}
