package ru.syudaev.flexscheduler.listener;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.syudaev.flexscheduler.service.SchedulerConditionService;
import ru.syudaev.kafkadto.SchedulerCommand;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static ru.syudaev.kafkadto.SchedulerCommand.PayloadKeys.TYPE;

/**
 * Потребитель сообщений kafka. Получает команду на снятие блокировки с шедулера.
 */
@Slf4j
@Component
@RequiredArgsConstructor
@ConditionalOnProperty(value = "spring.kafka.enabled", havingValue = "true")
public class KafkaSchedulerListenerImpl implements SchedulerListener {

    private final SchedulerConditionService conditionService;

    /**
     * Постребитель сообений kafka.
     *
     * @param command Сообщение kafka.
     */
    @KafkaListener(topics = "${spring.kafka.topic.name}",
            containerFactory = "schedulerListenerContainerFactory")
    public void listen(SchedulerCommand command) {
        log.info("Получена команда: {}", command);
        Optional.ofNullable(command.getPayload().get(TYPE.toLowerCase()))
                .ifPresent(conditionService::unlockScheduler);
    }
}
