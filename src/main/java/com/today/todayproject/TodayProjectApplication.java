package com.today.todayproject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class TodayProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(TodayProjectApplication.class, args);
	}

}
