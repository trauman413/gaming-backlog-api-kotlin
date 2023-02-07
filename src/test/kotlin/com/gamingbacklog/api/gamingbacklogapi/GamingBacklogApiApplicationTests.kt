package com.gamingbacklog.api.gamingbacklogapi

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@Import(GamingBacklogApiApplication::class)
@TestPropertySource(properties = ["CLIENT_ID=test_id", "CLIENT_SECRET=test_secret"])
class GamingBacklogApiApplicationTests {

	@Test
	fun contextLoads() {
	}

}
