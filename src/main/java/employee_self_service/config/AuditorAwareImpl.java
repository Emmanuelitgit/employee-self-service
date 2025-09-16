package employee_self_service.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware {
    @Override
    public Optional getCurrentAuditor() {
        String testId = SecurityContextHolder.getContext().getAuthentication().getName();
        return Optional.of(UUID.fromString(testId));
    }
}
