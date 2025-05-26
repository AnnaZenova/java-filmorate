package ru.yandex.practicum.filmorate.service.Event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventService {
    List<Event> getUserEvents(int userId);
}