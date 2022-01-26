package ru.syudaev.flexscheduler.service;

import ru.syudaev.flexscheduler.entity.Scheduler;
import ru.syudaev.flexscheduler.entity.enums.SchedulerType;
import net.logstash.logback.encoder.org.apache.commons.lang3.tuple.Pair;

import java.util.List;

/**
 * Собирает список шедулеров.
 */
public interface SchedulerListConfigurationService {
    List<Pair<SchedulerType, List<Scheduler>>> getSchedulers();
}
