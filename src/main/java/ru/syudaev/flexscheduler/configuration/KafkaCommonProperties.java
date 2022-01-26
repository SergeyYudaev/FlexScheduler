package ru.syudaev.flexscheduler.configuration;

import org.apache.kafka.clients.admin.AdminClientConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Тянет из application.yaml конфиги для kafka.
 */
@Component
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaCommonProperties {

    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    /**
     * Возвращает мапу с общими конфигами для продюсеров и консьюмеров kafka.
     *
     * @return Мапу с конфигами.
     */
    public Map<String, Object> getConfigurationProperties() {
        Map<String, Object> properties = new HashMap<>();

        properties.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);

        return properties;
    }
}
