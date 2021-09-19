package se.activemq.embedded.myactivemq.jms;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.annotation.DirtiesContext;

import javax.jms.JMSException;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static se.activemq.embedded.myactivemq.jms.ActiveMQConfig.ORDER_QUEUE;

@SpringBootTest
@DirtiesContext
public class ConsumerTest {

    @Autowired
    JmsTemplate jmsTemplate;

    String virtual_topic;

    @BeforeEach
    private void before() {
        virtual_topic = ORDER_QUEUE;
    }

    @Test
    public void convertAndSend() throws InterruptedException, JMSException {
        jmsTemplate.convertAndSend("vip."+virtual_topic, json("HELP", "Test!!!!!!!!!!!!"), m -> {     //  json("HELP", "Test!!!!!!!!!!!!")
            m.setStringProperty("EVENT", "REQUEST");
            m.setJMSCorrelationID(UUID.randomUUID().toString());
            return m;
        });
        jmsTemplate.setReceiveTimeout(3000);
        //TimeUnit.SECONDS.sleep(1);

        // QMessage
        Object msg = jmsTemplate.receiveAndConvert (virtual_topic); // ActiveMQTextMessage
        assertNotNull(msg);
        System.out.println(msg);
    }


    QMessage json(String type, String body){
        QHeader header = new QHeader(type);
        QMessage m = new QMessage(header, body);
        return m; // mapper.queueMessageToJson(m);
    }
}

