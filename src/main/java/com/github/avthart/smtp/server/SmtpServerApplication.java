package com.github.avthart.smtp.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmtpServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(SmtpServerApplication.class, args);
	}
}