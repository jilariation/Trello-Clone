package ru.jilly.trelloclone.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class KafkaConfig {
    @Value("${kafka-topic.auth}")
    private String authTopic;
    @Value("${kafka-topic.notification}")
    private String notificationTopic;
}
