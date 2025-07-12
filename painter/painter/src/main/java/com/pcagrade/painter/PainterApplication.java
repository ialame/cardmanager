package com.pcagrade.painter;

import com.pcagrade.mason.jpa.repository.EnableMasonRevisionRepositories;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication(exclude = {
		org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class,
		org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration.class,
		org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration.class
})
@EnableCaching
@EnableMasonRevisionRepositories
public class PainterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PainterApplication.class, args);
	}

}
