package com.example.docuflow.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {
    public static final String FILE_QUEUE = "file-processing-queue";
    public static final String PACKAGE_NAME = "com.example.docuflow.dto";

    // used to convert FileMessage to/from JSON
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages(PACKAGE_NAME);
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @Bean
    public Queue fileProcessingQueue() {
        return new Queue(FILE_QUEUE, true);  // true = durable queue
    }
}
