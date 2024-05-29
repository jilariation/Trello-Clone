package ru.jilly.trelloclone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class TrelloCloneApplication {

	public static void main(String[] args) {
		SpringApplication.run(TrelloCloneApplication.class, args);
	}

}
