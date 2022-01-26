package ru.syudaev.flexscheduler.service.impl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import ru.syudaev.flexscheduler.entity.CronScheduler;
import ru.syudaev.flexscheduler.entity.FixedDelayScheduler;
import ru.syudaev.flexscheduler.entity.Scheduler;
import ru.syudaev.flexscheduler.entity.enums.SchedulerType;
import ru.syudaev.flexscheduler.service.SchedulerListConfigurationService;
import net.logstash.logback.encoder.org.apache.commons.lang3.tuple.Pair;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Тянет списки шедулеров из application.yaml.
 */
@Getter
@Setter
@Component
@AllArgsConstructor
@ConditionalOnProperty(value = "application.scheduler-initialisation-type", havingValue = "yaml")
@ConfigurationProperties(prefix = "application.schedulers")
public class YamlSchedulerListConfigurationServiceImpl implements SchedulerListConfigurationService {

    private List<FixedDelayScheduler> fixedDelaySchedulers;
    private List<CronScheduler> cronSchedulers;

    @Override
    public List<Pair<SchedulerType, List<Scheduler>>> getSchedulers() {
        return List.of(
                Pair.of(SchedulerType.FIXED_DELAY, fixedDelaySchedulers.stream()
                        .map(sc -> (Scheduler) sc)
                        .collect(Collectors.toList())),
                Pair.of(SchedulerType.CRON, cronSchedulers.stream()
                        .map(sc -> (Scheduler) sc)
                        .collect(Collectors.toList()))

        );
    }
}
