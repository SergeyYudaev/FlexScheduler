package ru.syudaev.flexscheduler.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Шедулер, параметром которого является cron.
 */
@Getter
@Setter
public class CronScheduler extends Scheduler {
    /**
     * Значение cron. Пример: "0/5 * * * * ?" (отрабатывает каждые 5 сек.).
     */
    private String cron;

    @Override
    public String getSchedulerParameter() {
        return cron;
    }
}
