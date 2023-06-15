package com.social;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author ayameen
 */

@EnableScheduling
@SpringBootApplication
public class StoryMsV1Application {
	
	public static void main(String[] args) {
		SpringApplication.run(StoryMsV1Application.class, args);
	}

}
