package se.activemq.embedded.myactivemq;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ApplicationTests {

	@Autowired
	JmsTemplate jmsTemplate;

	@Test
	void contextLoads() {
		assertNotNull(jmsTemplate);
	}

}
