package gr.uagean.dsIss;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DsIssApplication {

	public static void main(String[] args) {
		SpringApplication.run(DsIssApplication.class, args);
	}
}
