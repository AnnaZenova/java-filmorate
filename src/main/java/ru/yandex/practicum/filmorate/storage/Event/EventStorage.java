package ru.yandex.practicum.filmorate.storage.Event;

import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;

import java.util.List;

public interface EventStorage {
    List<Event> getFeedList(int userId);

    Event addEvent(int userId, EventEnum eventType, OperationEnum operationEnum, int entityId);
}
