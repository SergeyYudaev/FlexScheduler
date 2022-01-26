package ru.syudaev.flexscheduler.configuration;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

/**
 * Конфиг продюсера kafka.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaProducerConfig {

    private static final String ACKS_CONFIG_VALUE = "all";

    private final KafkaCommonProperties commonProperties;

    /**
     * Конфигурирует ProducerFactory, которая является стратегией для создания продюсеров.
     *
     * @param <T> Тип сообщения.
     *
     * @return DefaultKafkaProducerFactory, реализация ProducerFactory.
     */
    @Bean
    public <T> ProducerFactory<String, T> producerFactory() {
        Map<String, Object> configProperties = commonProperties.getConfigurationProperties();

        configProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        configProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        configProperties.put(ProducerConfig.ACKS_CONFIG, ACKS_CONFIG_VALUE);

        return new DefaultKafkaProducerFactory<>(configProperties);
    }

    @Bean
    public <T> KafkaTemplate<String, T> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
