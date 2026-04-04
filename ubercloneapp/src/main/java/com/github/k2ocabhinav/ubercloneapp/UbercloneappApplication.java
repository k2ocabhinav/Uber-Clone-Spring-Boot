package com.github.k2ocabhinav.ubercloneapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class UbercloneappApplication {

	public static void main(String[] args) {
		SpringApplication.run(UbercloneappApplication.class, args);
	}

}
