package se.activemq.embedded.myactivemq.jms;

import org.apache.activemq.command.ActiveMQTextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;

import static se.activemq.embedded.myactivemq.jms.ActiveMQConfig.ORDER_QUEUE;

@Component
public class OrderConsumer {

    private static Logger log = LoggerFactory.getLogger(OrderConsumer.class);

    OrderProducer orderProducer;

    public OrderConsumer (OrderProducer orderProducer){
        this.orderProducer = orderProducer;
    }
    // "vip."+
    @JmsListener(destination = "vip."+ORDER_QUEUE,
            selector = "EVENT = 'REQUEST'")
    public void receiveMessage(@Payload Object payload,
                                @Headers MessageHeaders headers,
                               ActiveMQTextMessage message) throws JMSException {
        log.info("received <" + message.getText() + ">");

        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("######          Message Details           #####");
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");
        log.info("headers: " + headers);
        log.info("message: " + message);
        //log.info("session: " + session);
        log.info("- - - - - - - - - - - - - - - - - - - - - - - -");

        orderProducer.send(message.getText(), message.getCorrelationId());
    }

}