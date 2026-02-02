package de.jodabyte.apisecurity.jwt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BrokenJwtAuthenticationApplication {

	public static void main(String[] args) {
		SpringApplication.run(BrokenJwtAuthenticationApplication.class, args);
	}

}
