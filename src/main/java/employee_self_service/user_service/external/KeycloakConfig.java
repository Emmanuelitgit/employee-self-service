package employee_self_service.user_service.external;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class KeycloakConfig {

    private final KeycloakProperties keycloakProperties;

    public static Keycloak keycloak = null;
    @Autowired
    public KeycloakConfig(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    public Keycloak getInstance(){
        if (keycloak==null){
            keycloak = KeycloakBuilder
                    .builder()
                    .realm(keycloakProperties.getRealm())
                    .clientId(keycloakProperties.getClientId())
                    .clientSecret(keycloakProperties.getClientSecret())
                    .grantType(OAuth2Constants.PASSWORD)
                    .password(keycloakProperties.getPassword())
                    .username(keycloakProperties.getUsername())
                    .serverUrl(keycloakProperties.getServerUrl())
                    .resteasyClient(new ResteasyClientBuilder()
                            .connectionPoolSize(10)
                            .build())
                    .build();
        }
        return keycloak;
    }


}
