package com.shingle.user_auth_cas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@ComponentScan("com.shingle.user")
@SpringBootApplication
public class UserAuthCasApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserAuthCasApplication.class, args);
	}

}
