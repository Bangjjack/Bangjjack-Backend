package com.project.bangjjack;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class BangjjackApplication {

	public static void main(String[] args) {
		String timezone = System.getenv().getOrDefault("APP_TIMEZONE", "Asia/Seoul");
		TimeZone.setDefault(TimeZone.getTimeZone(timezone));
		SpringApplication.run(BangjjackApplication.class, args);
	}

}
