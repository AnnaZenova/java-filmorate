package ru.yandex.practicum.filmorate.storage.Event;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventStorage {
    Event createEvent(Event event);

    List<Event> getUserEvents(int userId);

    Event getEventById(int evenId) throws NotFoundException;
}