package ru.yandex.practicum.filmorate.storage.Event;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
@RequiredArgsConstructor
public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .timestamp(rs.getTimestamp("timestamps").getTime())
                .userId(rs.getLong("user_id"))
                .eventType(setEventType(rs.getString("event_type")))
                .eventId(rs.getLong("event_id"))
                .entityId(rs.getLong("entity_id"))
                .operation(setOperation(rs.getString("operation_type")))
                .build();
    }

    private OperationType setOperation(String operation) {
        switch (operation) {
            case "ADD":
                return OperationType.ADD;
            case "UPDATE":
                return OperationType.UPDATE;
            case "REMOVE":
                return OperationType.REMOVE;
            default:
                throw new WrongDataException("Операция не найдена");
        }
    }

    private EventType setEventType(String eventType) {
        switch (eventType) {
            case "LIKE":
                return EventType.LIKE;
            case "REVIEW":
                return EventType.REVIEW;
            case "FRIEND":
                return EventType.FRIEND;
            default:
                throw new WrongDataException("Тип события не определён");
        }
    }
}

