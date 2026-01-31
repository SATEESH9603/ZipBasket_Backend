package com.example.onlinetest.onlinetestbackendservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.example.onlinetest")
@EnableJpaRepositories(basePackages = "com.example.onlinetest.Repo")
@EntityScan(basePackages = "com.example.onlinetest.Repo")
@ConfigurationPropertiesScan(basePackages = "com.example.onlinetest")
public class OnlinetestbackendserviceApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(OnlinetestbackendserviceApplication.class, args);
	}
 
}




