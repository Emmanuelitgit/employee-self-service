package employee_self_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableScheduling
@EnableMethodSecurity
public class EmployeeSelfServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(EmployeeSelfServiceApplication.class, args);
	}

}
