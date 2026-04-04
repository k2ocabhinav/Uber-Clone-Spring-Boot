package com.github.k2ocabhinav.ubercloneapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableScheduling
public class UbercloneappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UbercloneappApplication.class, args);
	}

}
