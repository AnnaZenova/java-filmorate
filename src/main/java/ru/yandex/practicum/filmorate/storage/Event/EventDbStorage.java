package ru.yandex.practicum.filmorate.storage.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.PreparedStatement;
import java.util.*;

@Slf4j
@Component
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;
    private final EventMapper eventMapper;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate, EventMapper eventMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.eventMapper = eventMapper;
    }

    @Override
    public Event createEvent(int userId, EventType eventType, OperationType operationType, int entityId) {
        Event event = Event.builder()
                .eventId(0)
                .userId(userId)
                .eventType(eventType)
                .operationType(operationType)
                .entityId(entityId)
                .timestamp(System.currentTimeMillis())
                .build();

        final String sql = "INSERT INTO events (user_id, event_type, operation_type, entity_id, timestamps) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"event_id"});
            stmt.setInt(1, event.getUserId());
            stmt.setString(2, event.getEventType().toString());
            stmt.setString(3, event.getOperationType().toString());
            stmt.setInt(4, event.getEntityId());
            stmt.setLong(5, event.getTimestamp());
            return stmt;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            event.setEventId((Integer) keyHolder.getKey());
        }
        log.info("Событие с идентификатором {} создано", event.getEventId());
        return event;
    }

    @Override
    public Collection<Event> getEventByUserId(int userId) {
        final String sql = "SELECT * FROM events WHERE user_id = ?";
        log.info("Лента событий пользователя с идентификатором {} отправлена", userId);
        return jdbcTemplate.query(sql, eventMapper, userId);
    }
}