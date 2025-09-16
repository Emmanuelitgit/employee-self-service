package employee_self_service.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware {
    @Override
    public Optional getCurrentAuditor() {
        String testId = "33712b37-466e-4b68-98b6-639f8888a8d6";
        return Optional.of(UUID.fromString(testId));
    }
}
