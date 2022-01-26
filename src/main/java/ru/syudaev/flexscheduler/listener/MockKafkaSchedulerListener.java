package ru.syudaev.flexscheduler.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import ru.syudaev.kafkadto.SchedulerCommand;
import ru.syudaev.flexscheduler.producer.MockKafkaSchedulerMessageSender;

import static ru.syudaev.kafkadto.SchedulerCommand.CommandType.UNLOCK_SCHEDULER;

/**
 * Имитирует внешний сервис. Потребляет сообщение Kafka, отправляет команду на разблокировку шедулера.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class MockKafkaSchedulerListener {

    private final MockKafkaSchedulerMessageSender sender;

    /**
     * Постребитель сообений kafka.
     *
     * @param command Сообщение kafka.
     */
    @KafkaListener(topics = "${spring.kafka.topic.name}",
            containerFactory = "mockSchedulerListenerContainerFactory")
    public void listen(SchedulerCommand command) {
        log.warn("Имитатором внешнего сервиса получена команда: {}", command);

        if (command.getLockable()) {
            command.setCommandType(UNLOCK_SCHEDULER);
            sender.send(command);
        }
    }
}
