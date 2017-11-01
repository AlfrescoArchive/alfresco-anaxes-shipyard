package org.alfresco.deployment.sample;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;


@SpringBootApplication
@Import({CorsConfiguration.class})
public class Application
{
	public static void main(String[] args)
	{
		SpringApplication.run(Application.class, args);
	}

	@Bean
	public CommandLineRunner init(HelloTextRepository repository) {
		return (args) -> {
			// create the default welcome message
			repository.save(new HelloText("welcome", "Hello World!"));
		};
	}
}