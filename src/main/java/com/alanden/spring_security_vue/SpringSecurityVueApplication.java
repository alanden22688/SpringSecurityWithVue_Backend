package com.alanden.spring_security_vue;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

//@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.alanden")
@EntityScan(basePackages = "com.alanden")
@ComponentScan(basePackages = "com.alanden")
@EnableAutoConfiguration
@Configuration
public class SpringSecurityVueApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityVueApplication.class, args);
	}

}
