package ru.syudaev.flexscheduler.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Шедулер, параметром которого является время (мс) между окончанием выполнения команды и следующим вызовом шедулера.
 */
@Getter
@Setter
public class FixedDelayScheduler extends Scheduler {
    /**
     * Время (мс) между окончанием выполнения команды и следующим вызовом шедулера.
     */
    private Integer fixedDelay;

    @Override
    public String getSchedulerParameter() {
        return String.valueOf(fixedDelay);
    }
}
