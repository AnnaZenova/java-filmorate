package ru.yandex.practicum.filmorate.service.Event;

import ru.yandex.practicum.filmorate.model.Event;

import java.util.List;

public interface EventService {
    public List<Event> getUserEvents(int userId);
}