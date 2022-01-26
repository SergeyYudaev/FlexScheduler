package ru.syudaev.flexscheduler.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.syudaev.flexscheduler.listener.SchedulerListener;
import ru.syudaev.flexscheduler.producer.SchedulerMessageSender;

@Slf4j
@Configuration
public class MissingBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean(value = SchedulerListener.class)
    public SchedulerListener schedulerListener() {
        return command -> log.warn("Заглушка для SchedulerListener. Нет доступной конфигурации. command: {}", command);
    }

    @Bean
    @ConditionalOnMissingBean(value = SchedulerMessageSender.class)
    public SchedulerMessageSender schedulerMessageSender() {
        return command -> log.warn("Заглушка для SchedulerMessageSender. Нет доступной конфигурации. command: {}", command);
    }
}
