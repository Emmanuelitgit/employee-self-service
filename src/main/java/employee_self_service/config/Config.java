package employee_self_service.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableConfigurationProperties
@EnableWebSecurity
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
@EnableMethodSecurity(
        securedEnabled = true,
        prePostEnabled = true
)
public class Config {

    private final JwtAuthConverter jwtAuthConverter;

    @Autowired
    public Config(JwtAuthConverter jwtAuthConverter) {
        this.jwtAuthConverter = jwtAuthConverter;
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.authorizeHttpRequests((auth)->{
            auth.requestMatchers("/swagger-ui/*");
            auth.anyRequest().permitAll();
        })
                .csrf((AbstractHttpConfigurer::disable))
                .cors((AbstractHttpConfigurer::disable))
                .oauth2ResourceServer((auth->{
                    auth.jwt(jwt->{
                        jwt.jwtAuthenticationConverter(jwtAuthConverter);
                    });
                }))
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return  new BCryptPasswordEncoder();
    }

    @Bean
    AuditorAware auditorAware(){
        return new AuditorAwareImpl();
    }
}
