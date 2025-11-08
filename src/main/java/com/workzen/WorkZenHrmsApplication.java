package com.workzen;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(scanBasePackages = "com.workzen")
@EnableJpaRepositories(basePackages = "com.workzen.repository")
@EntityScan(basePackages = "com.workzen.entity")
@EnableScheduling
public class WorkZenHrmsApplication {

	public static void main(String[] args) {
		SpringApplication.run(WorkZenHrmsApplication.class, args);
	}

}
