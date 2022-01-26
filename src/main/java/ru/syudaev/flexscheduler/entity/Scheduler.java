package ru.syudaev.flexscheduler.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Базовый класс для шедулеров.
 */
@Getter
@Setter
public abstract class Scheduler {
    /**
     * Команда, которая публикуется в kafka и запускает джобу во внешнем сервисе.
     */
    protected String command;
    /**
     * Признак активности шедулера (вкл-вкл).
     */
    protected Boolean enabled;

    public abstract String getSchedulerParameter();
}
