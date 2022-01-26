package ru.syudaev.flexscheduler.producer;

import ru.syudaev.dto.SchedulerCommand;

/**
 * Сервис, отправляющий сообщения о запуске джобы во внешнем сервисе.
 */
public interface SchedulerMessageSender {

    public void send(SchedulerCommand dto);
}
