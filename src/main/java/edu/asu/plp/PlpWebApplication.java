package edu.asu.plp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableOAuth2Sso
@ImportResource("spring-module.xml")
public class PlpWebApplication{
	public static void main(String[] args) {
		SpringApplication.run(PlpWebApplication.class, args);
	}
}
