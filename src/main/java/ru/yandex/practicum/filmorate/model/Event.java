package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

@Data
@Builder
public class Event {
    @PastOrPresent
    private long timestamp;
    @NotNull
    private long userId;
    @NotNull
    private EventType eventType;
    @NotNull
    private OperationType operation;
    private long eventId;
    @NotNull
    private long entityId;
}