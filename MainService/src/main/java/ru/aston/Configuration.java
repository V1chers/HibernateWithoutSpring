package ru.aston;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;

@org.springframework.context.annotation.Configuration
public class Configuration {

    @Bean
    public NewTopic createTopic() {
        return TopicBuilder.name("notification")
                .partitions(1)
                .replicas(1)
                .build();
    }
}
