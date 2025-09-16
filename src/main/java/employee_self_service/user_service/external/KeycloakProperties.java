package employee_self_service.user_service.external;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "keycloak")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KeycloakProperties {
    private String clientId;
    private String clientSecret;
    private String serverUrl;
    private String password;
    private String username;
    private String grantType;
    private String realm;
}
