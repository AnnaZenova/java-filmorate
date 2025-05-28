package ru.yandex.practicum.filmorate.storage.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    @Override
    public void createEvent(int userId, EventType eventType, OperationType operationType, int entityId) {
        String sql = "INSERT INTO events (user_id, event_type, operation_type, entity_id) VALUES (?,?,?,?)";
        jdbcTemplate.update(sql, userId, eventType.toString(), operationType.toString(), entityId);
    }

    @Override
    public Collection<Event> getEventByUserId(int userId) {
        final String sql = "SELECT * FROM events WHERE user_id = ?";
        return jdbcTemplate.query(sql, eventMapper, userId);
    }
}