package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;
import ru.yandex.practicum.filmorate.model.enums.EventEnum;

@Data
@Builder
@AllArgsConstructor
public class Event {
    private int id;
    private int userId;
    private long timestamp;
    private EventEnum eventType;
    private OperationEnum operation;
    private int entityId;
}
