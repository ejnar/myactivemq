package se.activemq.embedded.myactivemq.jms;

import org.apache.activemq.ScheduledMessage;
import org.apache.activemq.command.ActiveMQTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

import static se.activemq.embedded.myactivemq.jms.ActiveMQConfig.ORDER_QUEUE;

@Service
public class OrderProducer {

    private static Logger log = LoggerFactory.getLogger(OrderProducer.class);

    @Autowired
    private JmsTemplate jmsTemplate;

    public void send(Object message, String correlationId) {
        log.info("sending with convertAndSend() to queue <" + message + ">");
        jmsTemplate.convertAndSend(ORDER_QUEUE, message, m -> {
            m.setStringProperty("EVENT", "RESPONSE");
            m.setLongProperty(ScheduledMessage.AMQ_SCHEDULED_DELAY, 1000);
            m.setJMSCorrelationID(correlationId);
            return m;
        });
    }
}
