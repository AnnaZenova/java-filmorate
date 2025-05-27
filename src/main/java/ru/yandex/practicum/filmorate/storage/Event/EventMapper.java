package ru.yandex.practicum.filmorate.storage.Event;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class EventMapper implements RowMapper<Event> {

    @Override
    public Event mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(rs.getInt("event_id"))
                .userId(rs.getInt("user_id"))
                .eventType(EventType.valueOf(rs.getString("event_type")))
                .operationType(OperationType.valueOf(rs.getString("operation_type")))
                .entityId(rs.getInt("entity_id"))
                .timestamp(rs.getLong("timestamps"))
                .build();
    }
}

