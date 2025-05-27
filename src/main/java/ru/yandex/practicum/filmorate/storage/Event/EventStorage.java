package ru.yandex.practicum.filmorate.storage.Event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.util.Collection;

public interface EventStorage {
    Event createEvent(int userId, EventType eventType, OperationType operationType, int entityId);

    Collection<Event> getEventByUserId(int userId);
}