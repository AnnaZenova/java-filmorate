package ru.yandex.practicum.filmorate.storage.Event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Repository("EventDbStorage")
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public EventDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Event> getAllEvents() {
        String query = "SELECT * FROM events";
        return jdbcTemplate.query(query, this::mapRowToEvent);
    }

    @Override
    public Event createEvent(Event event) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("events")
                .usingGeneratedKeyColumns("event_id");
        event.setId(simpleJdbcInsert.executeAndReturnKey(event.toMap()).intValue());
        log.info("Добавлено новое событие с ID={}", event.getId());
        return event;
    }

    @Override
    public List<Event> getUserEvents(int id) {
        if (!eventExists(id)) {
            throw new NotFoundException("События пользователя с ID=" + id + " не найдены");
        }
        SqlRowSet eventRows = jdbcTemplate.queryForRowSet("SELECT event_id FROM events WHERE user_id = ? ", id);
        Set<Integer> eventsIds = new HashSet<>();
        while (eventRows.next()) {
            eventsIds.add(eventRows.getInt(1));
        }
        List<Event> events = new ArrayList<>();
        for (Integer eId : eventsIds) {
            events.add(getEventById(eId));
        }
        log.info("Получены события пользователя с ID={}", id);
        return events;
    }

    @Override
    public Event getEventById(int eventId) {
        Event event;
        SqlRowSet eventRows = jdbcTemplate.queryForRowSet("SELECT * FROM events WHERE event_id = ?", eventId);
        if (eventRows.first()) {
            event = new Event(
                    eventRows.getInt("event_id"),
                    eventRows.getInt("user_id"),
                    EventType.valueOf(eventRows.getString("event_type")),
                    OperationType.valueOf(eventRows.getString("operation")),
                    eventRows.getInt("entity_id"),
                    eventRows.getTimestamp("event_timestamp"));
            return event;
        } else {
            return null;
        }
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .id(resultSet.getInt("event_id"))
                .userId(resultSet.getInt("user_id"))
                .eventType(EventType.valueOf(resultSet.getString("event_type")))
                .operation(OperationType.valueOf(resultSet.getString("operation")))
                .entityId(resultSet.getInt("entity_id"))
                .timestamp(resultSet.getTimestamp("event_timestamp"))
                .build();
    }

    private boolean eventExists(int eventId) {
        String sql = "SELECT COUNT(*) FROM events WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, eventId);
        return count != null && count > 0;
    }
}