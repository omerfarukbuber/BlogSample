package com.omerfbuber.basicrestapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.omerfbuber.entities"})
@ComponentScan(basePackages = {
		"com.omerfbuber.repositories",
		"com.omerfbuber.controllers",
		"com.omerfbuber.services"})
@EnableJpaRepositories(basePackages = {"com.omerfbuber.repositories"})
@SpringBootApplication
public class BasicRestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(BasicRestApiApplication.class, args);
	}

}
