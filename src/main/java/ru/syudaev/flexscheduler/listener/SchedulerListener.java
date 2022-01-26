package ru.syudaev.flexscheduler.listener;

import ru.syudaev.kafkadto.SchedulerCommand;

public interface SchedulerListener {

    void listen(SchedulerCommand command);
}
