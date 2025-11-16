package org.orchestrator.individuals_api;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class IndividualsApiApplicationTests {

	@Test
	void contextLoads() {
	}

}
