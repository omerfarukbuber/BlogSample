package com.omerfbuber;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@EntityScan(basePackages = {"com.omerfbuber.entity"})
@ComponentScan(basePackages = {"com.omerfbuber.*"})
@EnableJpaRepositories(basePackages = {"com.omerfbuber.repository"})
@EnableCaching
@SpringBootApplication
public class BlogSample {

	public static void main(String[] args) {
		SpringApplication.run(BlogSample.class, args);
	}

}
