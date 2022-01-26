package ru.syudaev.flexscheduler.producer;

import lombok.extern.slf4j.Slf4j;
import ru.syudaev.kafkadto.SchedulerCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFutureCallback;

/**
 * Отправщик сообщений, использует kafka.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaSchedulerMessageSenderImpl implements SchedulerMessageSender {

    private final String topicName;
    private final KafkaTemplate<String, SchedulerCommand> kafkaTemplate;

    public KafkaSchedulerMessageSenderImpl(@Value("${spring.kafka.topic.name}") String topicName,
                                           KafkaTemplate<String, SchedulerCommand> kafkaTemplate) {
        this.topicName = topicName;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Метод для отправки сообщений посредством kafka.
     *
     * @param dto Полезная нагрузка сообщения. Cм. {@link SchedulerCommand}.
     */
    @Override
    public void send(SchedulerCommand dto) {
        var type = dto.getPayload().get(SchedulerCommand.PayloadKeys.TYPE.toLowerCase());
        var future = kafkaTemplate.send(topicName, type, dto);
        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onFailure(Throwable throwable) {
                log.error("Unable to send message = {} due to: {}", dto, throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, SchedulerCommand> stringDataSendResult) {
                log.info("Sent Message = {} with offset = {}", dto, stringDataSendResult.getRecordMetadata().offset());
            }
        });
    }
}
