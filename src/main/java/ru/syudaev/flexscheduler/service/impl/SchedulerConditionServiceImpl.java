package ru.syudaev.flexscheduler.service.impl;

import lombok.RequiredArgsConstructor;
import ru.syudaev.flexscheduler.entity.Scheduler;
import ru.syudaev.flexscheduler.entity.enums.SchedulerType;
import ru.syudaev.flexscheduler.service.SchedulerConditionService;
import ru.syudaev.flexscheduler.service.SchedulerListConfigurationService;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Сервис, хранящий состояние шедулеров (заблокирован/разблокирован) и управляет ими.
 * Блокировка шедулера необходима, чтобы задержка шедулера отсчитывалась после выполнения джобы
 * во внешнем сервисе. Также не позволяет забить топик командами шедулера с долгой джобой.
 * Данная реализация хранит состояние в памяти.
 */
@Component
@RequiredArgsConstructor
public class SchedulerConditionServiceImpl implements SchedulerConditionService {

    private static final List<SchedulerType> lockableSchedulerTypes = List.of(
            SchedulerType.FIXED_DELAY
    );

    private final SchedulerListConfigurationService schedulerListConfigurationService;

    private Map<String, Boolean> schedulerConditions;

    @Override
    public boolean isEnabled(String schedulerCommand) {
        return schedulerConditions.get(schedulerCommand);
    }

    @Override
    public void lockScheduler(String schedulerCommand) {
        schedulerConditions.put(schedulerCommand, false);
    }

    @Override
    public void unlockScheduler(String schedulerCommand) {
        schedulerConditions.put(schedulerCommand, true);
    }

    @EventListener
    private void onContextRefresh(ContextRefreshedEvent event) {
        schedulerConditions = new HashMap<>();
        schedulerListConfigurationService.getSchedulers()
                .forEach(pair -> {
                            if (lockableSchedulerTypes.contains(pair.getKey())) {
                                pair.getValue().stream()
                                        .filter(Scheduler::getEnabled)
                                        .forEach(sc ->
                                                schedulerConditions.put(sc.getCommand(), sc.getEnabled()));
                            }
                        }
                );
    }
}
