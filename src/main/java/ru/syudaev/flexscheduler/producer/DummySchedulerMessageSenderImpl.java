package ru.syudaev.flexscheduler.producer;

import lombok.extern.slf4j.Slf4j;
import ru.syudaev.dto.SchedulerCommand;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Заглушка для kafka.
 */
@Slf4j
@Component
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "false")
public class DummySchedulerMessageSenderImpl implements SchedulerMessageSender {

    @Override
    public void send(SchedulerCommand dto) {
        log.info("Для отправки сообщений необходимо включить kafka");
    }
}
