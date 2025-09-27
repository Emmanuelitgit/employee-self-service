package employee_self_service.payment_service.repo;

import employee_self_service.payment_service.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepo extends JpaRepository<Payment, UUID> {
    Optional<Payment> findByReference(String reference);
}
