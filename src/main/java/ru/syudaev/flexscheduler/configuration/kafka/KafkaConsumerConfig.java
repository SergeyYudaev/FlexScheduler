package ru.syudaev.flexscheduler.configuration.kafka;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import ru.syudaev.kafkadto.SchedulerCommand;
import ru.syudaev.flexscheduler.listener.MockKafkaSchedulerListener;

import java.util.HashMap;

/**
 * Конфиг кафка-потребителя.
 */
@Configuration
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaConsumerConfig {

    public static final String EARLIEST = "earliest";

    @Value("${spring.application.name}")
    private String groupId;

    private final KafkaCommonProperties commonProperties;

    /**
     * Конфигурирует ConsumerFactory, которая является стратегией создания Conusmer-ов.
     *
     * @return DefaultKafkaConsumerFactory, реализацию интерфейса ConsumerFactory.
     *         Содержит мапу с конфигами (сериализация/десериализация и проч.).
     */
    @Bean
    public ConsumerFactory<String, SchedulerCommand> schedulerListenerConsumerFactory() {
        var properties = new HashMap<>(commonProperties.getConfigurationProperties());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.syudaev.kafkadto.SchedulerCommand");
        return new DefaultKafkaConsumerFactory<>(properties);
    }

    /**
     * Конфигурирует ConcurrentKafkaListenerContainerFactory.
     * Конфиг содержит фильтр для сообщений (чтоб не потреблять свои собственные сообщения).
     *
     * @return ConcurrentKafkaListenerContainerFactory - фабрика, создающая контейнеры для методов,
     *         аннотированных {@literal @}KafkaListener.
     */
    @Bean("schedulerListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SchedulerCommand> schedulerListenerContainerFactory() {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, SchedulerCommand>();
        factory.setConsumerFactory(schedulerListenerConsumerFactory());
        factory.setRecordFilterStrategy(consumerRecord ->
                consumerRecord.value() == null
                        || consumerRecord.value().getCommandType().equals(SchedulerCommand.CommandType.EXECUTE_EXTERNAL_JOB)
        );
        return factory;
    }

    /**
     * Конфиг для имитатора внешнего сервиса (см. {@link MockKafkaSchedulerListener})
     *
     * @return ConcurrentKafkaListenerContainerFactory - фабрика, создающая контейнеры для методов,
     *         аннотированных {@literal @}KafkaListener.
     */
    @Bean("mockSchedulerListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, SchedulerCommand> mockSchedulerListenerContainerFactory() {
        var properties = new HashMap<>(commonProperties.getConfigurationProperties());
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, EARLIEST);
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "externalService");
        properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        properties.put(ErrorHandlingDeserializer.KEY_DESERIALIZER_CLASS, StringDeserializer.class);
        properties.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS, JsonDeserializer.class);
        properties.put(JsonDeserializer.VALUE_DEFAULT_TYPE, "ru.syudaev.kafkadto.SchedulerCommand");

        var factory = new ConcurrentKafkaListenerContainerFactory<String, SchedulerCommand>();
        factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(properties));
        factory.setRecordFilterStrategy(consumerRecord ->
                consumerRecord.value() == null
                        || consumerRecord.value().getCommandType().equals(SchedulerCommand.CommandType.UNLOCK_SCHEDULER)
        );
        return factory;
    }
}
