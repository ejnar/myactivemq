package se.activemq.embedded.myactivemq.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class PersonMessageConverter  implements MessageConverter {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(PersonMessageConverter.class);

    ObjectMapper mapper;

    public PersonMessageConverter() {
        mapper = new ObjectMapper();
    }

    @Override
    public Message toMessage(Object object, Session session)
            throws JMSException {
        QMessage qMessage = (QMessage) object;
        String payload = null;
        try {
            payload = mapper.writeValueAsString(qMessage);
            LOGGER.info("outbound json='{}'", payload);
        } catch (JsonProcessingException e) {
            LOGGER.error("error converting form person", e);
        }

        TextMessage message = session.createTextMessage();
        message.setText(payload);

        return message;
    }

    @Override
    public Object fromMessage(Message message) throws JMSException {
        TextMessage textMessage = (TextMessage) message;
        String payload = textMessage.getText();
        LOGGER.info("inbound json='{}'", payload);

        QMessage qMessage = null;
        try {
            qMessage = mapper.readValue(payload, QMessage.class);
        } catch (Exception e) {
            LOGGER.error("error converting to person", e);
        }

        return qMessage;
    }
}