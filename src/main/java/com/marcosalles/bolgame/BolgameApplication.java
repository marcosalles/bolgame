package com.marcosalles.bolgame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BolgameApplication {

	public static void main(String[] args) {
		SpringApplication.run(BolgameApplication.class, args);
	}
}
