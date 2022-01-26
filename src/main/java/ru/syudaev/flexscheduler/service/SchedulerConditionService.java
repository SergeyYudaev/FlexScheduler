package ru.syudaev.flexscheduler.service;

/**
 * Сервис, хранящий состояние шедулеров (заблокирован/разблокирован) и управляет ими.
 * Блокировка шедулера необходима, чтобы задержка шедулера отсчитывалась после выполнения джобы
 * во внешнем сервисе. Также не позволяет забить топик командами шедулера с долгой джобой.
 */
public interface SchedulerConditionService {

    boolean isEnabled(String schedulerCommand);

    void lockScheduler(String schedulerCommand);

    void unlockScheduler(String schedulerCommand);
}
