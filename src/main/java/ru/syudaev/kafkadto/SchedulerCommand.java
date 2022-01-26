package ru.syudaev.kafkadto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * Класс-DTO для передачи команды шедулера через kafka. Map используется, чтобы можно было
 * передавать дополнительные параметры шедулера, кроме типа шедулера (например, размер батча).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SchedulerCommand {

    @JsonProperty(value = "commandType")
    private CommandType commandType;

    @JsonProperty(value = "lockable")
    private Boolean lockable;

    @JsonProperty(value = "payload")
    private Map<String, String> payload;

    /**
     * Конструктор для исходящей команды шедулера, несущей только команду шедулера и признак блокировки в payload.
     *
     * @param schedulerCommand Команда шедулера.
     * @param lockable Признак блокировки шедулера (надо ли ждать исполнения внешней джобы).
     */
    public SchedulerCommand(String schedulerCommand, boolean lockable) {
        this.commandType = CommandType.EXECUTE_EXTERNAL_JOB;
        this.lockable = lockable;
        this.payload = new HashMap<>();
        payload.put(PayloadKeys.TYPE.toLowerCase(), schedulerCommand);
    }

    /**
     * Ключи для мапы payload.
     */
    public enum PayloadKeys {
        TYPE;

        public String toLowerCase() {
            return toString().toLowerCase();
        }
    }

    /**
     * Тип команды (запрос/ответ).
     */
    public enum CommandType {
        EXECUTE_EXTERNAL_JOB,
        UNLOCK_SCHEDULER
    }
}
