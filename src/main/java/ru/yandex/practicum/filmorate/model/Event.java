package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.Timestamp;

@Data
@Builder
public class Event {
    private int eventId;
    @NotNull
    private int userId;
    @NotNull
    @NotBlank
    private EventType eventType;
    @NotNull
    @NotBlank
    private OperationType operationType;
    @NotNull
    private int entityId;
    @Positive
    private long timestamp;
}