package ru.syudaev.flexscheduler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.syudaev.flexscheduler.entity.enums.SchedulerType;
import ru.syudaev.flexscheduler.producer.SchedulerMessageSender;
import ru.syudaev.kafkadto.SchedulerCommand;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Service;
import ru.syudaev.flexscheduler.entity.Scheduler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * Сервис, который инициализирует шедулеры при запуске приложения.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SchedulersInitializationService {

    private final Map<SchedulerType, Consumer<List<Scheduler>>> schedulerInitializers = Map.of(
            SchedulerType.FIXED_DELAY, this::configureFixedDelaySchedulers,
            SchedulerType.CRON, this::configureCronSchedulers
    );

    /**
     * Изначальная задержка для всех шедулеров. Необходима, чтобы при обновлении стенда две поды не могли слать одинаковые команды.
     */
    @Value("${application.initial-scheduler-delay}")
    private Integer initialSchedulersDelay;

    private final ThreadPoolTaskScheduler scheduler;
    private final SchedulerListConfigurationService schedulerConfiguration;
    private final SchedulerMessageSender sender;
    private final SchedulerConditionService schedulerConditionService;

    /**
     * Инициализация шедулеров при старте приложения.
     *
     * @param event ContextRefreshedEvent event.
     */
    @EventListener
    private void onContextRefresh(ContextRefreshedEvent event) {
        scheduler.schedule(() ->
                        schedulerConfiguration.getSchedulers()
                                .forEach(pair ->
                                        Optional.ofNullable(schedulerInitializers.get(pair.getKey()))
                                                .ifPresent(consumer -> consumer.accept(pair.getValue()))
                                ),
                new Date(System.currentTimeMillis() + initialSchedulersDelay));
    }

    private void configureFixedDelaySchedulers(List<Scheduler> schedulers) {
        schedulers.stream()
                .filter(Scheduler::getEnabled)
                .forEach(sc -> this.scheduler.scheduleWithFixedDelay(
                        () -> doFixedDelaySchedulerJob(sc),
                        Integer.parseInt(sc.getSchedulerParameter()))
                );
    }

    private void configureCronSchedulers(List<Scheduler> schedulers) {
        schedulers.stream()
                .filter(Scheduler::getEnabled)
                .forEach(sc -> {
                    CronTrigger trigger = new CronTrigger(sc.getSchedulerParameter());
                    this.scheduler.schedule(
                            () -> sender.send(new SchedulerCommand(sc.getCommand(), false)),
                            trigger);
                });
    }

    private void doFixedDelaySchedulerJob(Scheduler scheduler) {
        if (schedulerConditionService.isEnabled(scheduler.getCommand())) {
            sender.send(new SchedulerCommand(scheduler.getCommand(), true));
            schedulerConditionService.lockScheduler(scheduler.getCommand());
        }
    }
}
