package se.activemq.embedded.myactivemq.jms;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.RedeliveryPolicy;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.config.JmsListenerContainerFactory;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.support.converter.MappingJackson2MessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;
import org.springframework.jms.support.converter.SimpleMessageConverter;
import org.springframework.util.ErrorHandler;

import javax.jms.ConnectionFactory;
import javax.jms.Session;

@EnableJms
@Configuration
public class ActiveMQConfig {

    private static final Logger logger = LogManager.getLogger(ActiveMQConfig.class);

    public static final String ORDER_QUEUE = "VirtualTopic.iiu.common.v1";

    @Value("${spring.activemq.broker-url}")
    private String brokerUrl;
    @Value("${spring.activemq.useRetroactiveConsumer}")
    private boolean useRetroactiveConsumer;
    @Value("${spring.activemq.initialRedeliveryDelay}")
    private int initialRedeliveryDelay;
    @Value("${spring.activemq.redeliveryDelay}")
    private int redeliveryDelay;
    @Value("${spring.activemq.maximumRedeliveries}")
    private int maximumRedeliveries;
    @Value("${spring.activemq.backOffMultiplier}")
    private int backOffMultiplier;
    @Value("${spring.activemq.useExponentialBackOff}")
    private boolean useExponentialBackOff;
    @Value("${spring.activemq.maximumRedeliveryDelay}")
    private long maximumRedeliveryDelay;


    @Bean
    public ConnectionFactory connectionFactory(){
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory();
        activeMQConnectionFactory.setBrokerURL(brokerUrl);
//        activeMQConnectionFactory.setUserName(user);
//        activeMQConnectionFactory.setPassword(password);
        activeMQConnectionFactory.setRedeliveryPolicy(redeliveryPolicy());
        activeMQConnectionFactory.setNonBlockingRedelivery(true);
        activeMQConnectionFactory.setUseRetroactiveConsumer(useRetroactiveConsumer);
        return activeMQConnectionFactory;
    }

    private RedeliveryPolicy redeliveryPolicy() {
        RedeliveryPolicy redeliveryPolicy = new RedeliveryPolicy();
        redeliveryPolicy.setInitialRedeliveryDelay(initialRedeliveryDelay);
        redeliveryPolicy.setRedeliveryDelay(redeliveryDelay);
        redeliveryPolicy.setMaximumRedeliveries(maximumRedeliveries);
        redeliveryPolicy.setBackOffMultiplier(backOffMultiplier);
        redeliveryPolicy.setUseExponentialBackOff(useExponentialBackOff);
        redeliveryPolicy.setMaximumRedeliveryDelay(maximumRedeliveryDelay);
        return redeliveryPolicy;
    }

    @Bean
    public CachingConnectionFactory cachingConnectionFactory() {
        CachingConnectionFactory cachingConnectionFactory =
                new CachingConnectionFactory(connectionFactory());
        cachingConnectionFactory.setSessionCacheSize(6);
        cachingConnectionFactory.setCacheConsumers(true);
//        cachingConnectionFactory.setCacheProducers(true);
        return cachingConnectionFactory;
    }

    @Bean
    public JmsTemplate orderJmsTemplate() {
        JmsTemplate jmsTemplate =
                new JmsTemplate(cachingConnectionFactory());
        jmsTemplate.setMessageConverter(jacksonJmsMessageConverter());
        jmsTemplate.setPubSubDomain(true);
//        jmsTemplate.setReceiveTimeout(1000);
        return jmsTemplate;
    }

    @Bean // Serialize message content to json using TextMessage
    public MappingJackson2MessageConverter jacksonJmsMessageConverter() {
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");
        converter.setObjectMapper(objectMapper());
        //PersonMessageConverter converter = new PersonMessageConverter();
        return converter;
    }

    @Bean
    public ObjectMapper objectMapper(){
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public JmsListenerContainerFactory jmsListenerContainerFactory()
    {
        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrency("1-2");
        factory.setErrorHandler(getErrorHandler());
        factory.setSessionAcknowledgeMode(Session.AUTO_ACKNOWLEDGE);
        factory.setMessageConverter(jacksonJmsMessageConverter());
        factory.setPubSubDomain(true);
        return factory;
    }

    private ErrorHandler getErrorHandler()
    {
        return exception -> logger.error("Exception thrown from consumer", exception);
    }

}

