package com.projectpal.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
public class DataSourceConfig {

	@Bean
	@Profile("development")
	HikariDataSource developmentDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/projectpal");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

	@Bean
	@Profile("production")
	HikariDataSource productionDataSource() {
		HikariDataSource dataSource = new HikariDataSource();
		dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
		dataSource.setJdbcUrl("jdbc:mysql://localhost:3306/projectpal");
		dataSource.setUsername("root");
		dataSource.setPassword("root");
		return dataSource;
	}

}
