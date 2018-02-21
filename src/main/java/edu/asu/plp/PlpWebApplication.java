package edu.asu.plp;

import org.apache.log4j.PropertyConfigurator;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.context.annotation.ImportResource;

@SpringBootApplication
@EnableOAuth2Sso
@ImportResource("spring-module.xml")
public class PlpWebApplication {
	public static void main(String[] args) {
        PropertyConfigurator.configure("log4j.properties");
		SpringApplication.run(PlpWebApplication.class, args);
	}
}
