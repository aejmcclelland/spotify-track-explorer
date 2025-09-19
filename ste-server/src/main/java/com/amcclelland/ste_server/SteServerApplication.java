package com.amcclelland.ste_server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.amcclelland.ste_server.config.SpotifyProperties;

@SpringBootApplication
@EnableConfigurationProperties(SpotifyProperties.class)
public class SteServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SteServerApplication.class, args);
	}

}
