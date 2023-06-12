package com.projectpal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories("com.projectpal.repository")
@EnableTransactionManagement
@EnableScheduling
public class ProjectpalApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectpalApplication.class, args);
	}

}
