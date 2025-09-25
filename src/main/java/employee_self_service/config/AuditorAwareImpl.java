package employee_self_service.config;

import employee_self_service.user_service.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuditorAwareImpl implements AuditorAware {

    public String getAuthenticatedUserId(@Autowired UserRepo userRepo){
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        UUID userId = userRepo.getUserId(username);
        return userId.toString();
    }

    @Override
    public Optional getCurrentAuditor() {
        return Optional.of(UUID.randomUUID());
    }
}
