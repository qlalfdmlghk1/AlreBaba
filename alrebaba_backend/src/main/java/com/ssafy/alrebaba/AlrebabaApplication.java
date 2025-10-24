package com.ssafy.alrebaba;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class AlrebabaApplication {

	public static void main(String[] args) {
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
		SpringApplication.run(AlrebabaApplication.class, args);
	}

}
