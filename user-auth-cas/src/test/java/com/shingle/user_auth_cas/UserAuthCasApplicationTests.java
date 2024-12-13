package com.shingle.user_auth_cas;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class UserAuthCasApplicationTests {

	@Test
	void contextLoads() {
	}

}
