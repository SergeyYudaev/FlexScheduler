package ru.syudaev.flexscheduler.producer;

import ru.syudaev.kafkadto.SchedulerCommand;

/**
 * Сервис, отправляющий сообщения о запуске джобы во внешнем сервисе.
 */
public interface SchedulerMessageSender {

    public void send(SchedulerCommand dto);
}
