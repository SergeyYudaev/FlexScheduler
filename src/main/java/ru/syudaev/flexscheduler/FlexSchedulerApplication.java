package ru.syudaev.flexscheduler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Сервис с абстрактными шедулерами (добавляются через application.yaml).
 * Шедулеры отправляют команды на запуск запланированных задач в других сервисах.
 */
@SpringBootApplication
public class FlexSchedulerApplication {

    public static void main(String[] args) {
        SpringApplication.run(FlexSchedulerApplication.class, args);
    }

}
