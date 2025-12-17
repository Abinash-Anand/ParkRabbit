package com.parkrabbit.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.github.cdimascio.dotenv.Dotenv;
@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
        dotenv.entries().forEach(e ->
            System.setProperty(e.getKey(), e.getValue())
        );

		SpringApplication.run(BackendApplication.class, args);
	}

}
