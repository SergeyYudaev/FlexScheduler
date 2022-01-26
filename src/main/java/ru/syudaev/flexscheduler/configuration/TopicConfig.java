package ru.syudaev.flexscheduler.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

/**
 * Класс, создающий топик kafka при его отстуствии.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class TopicConfig {

    private final KafkaCommonProperties commonProperties;

    /**
     * Метод, конфигурирующий KafkaAdmin.
     *
     * @return KafkaAdmin.
     */
    @Bean
    public KafkaAdmin kafkaAdmin() {
        return new KafkaAdmin(commonProperties.getConfigurationProperties());
    }

    /**
     * Метод, создающий топик kafka при его отстуствии.
     *
     * @param topicName Имя топика, задается в application.yaml.
     * @param partitions Количество партиций топика, задается в application.yaml.
     * @param replicas Фактор репликации топика, задается в application.yaml.
     * @return Топик.
     */
    @Bean
    public NewTopic topic(
            @Value("${spring.kafka.topic.name}") String topicName,
            @Value("${spring.kafka.topic.partitions}") Integer partitions,
            @Value("${spring.kafka.topic.replicas}") Integer replicas
    ) {
        return TopicBuilder
                .name(topicName)
                .partitions(partitions)
                .replicas(replicas)
                .build();
    }
}
