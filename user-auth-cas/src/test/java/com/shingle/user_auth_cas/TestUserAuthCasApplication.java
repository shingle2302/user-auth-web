package com.shingle.user_auth_cas;

import org.springframework.boot.SpringApplication;

public class TestUserAuthCasApplication {

	public static void main(String[] args) {
		SpringApplication.from(UserAuthCasApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
