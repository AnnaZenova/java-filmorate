package ru.yandex.practicum.filmorate.storage.Event;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.enums.EventEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Repository
@AllArgsConstructor
public class EventDbStorage implements EventStorage {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Event> getFeedList(int userId) {
        log.info("Хранилище: выполнение запроса к эндпоинту: '/users' на получение ленты событий");
        String sql = "SELECT * FROM events WHERE user_id = ?";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> mapRowToEvent(resultSet, rowNum), userId);
    }

    @Override
    public Event addEvent(int userId, EventEnum eventType, OperationEnum operationEnum, int entityId) {
        /*if (!userExists(userId)){
            throw new NotFoundException("Юзера с таким id нет !");
        }*/

        KeyHolder holder = new GeneratedKeyHolder();
        Event event = Event.builder()
                .userId(userId)
                .timestamp(System.currentTimeMillis())
                .eventType(eventType)
                .operation(operationEnum)
                .entityId(entityId)
                .build();

        String sql = "INSERT INTO events (user_id, timestamps, event_type, operation_type, entity_id) VALUES(?, ?, ?, ?, ?)";
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, event.getUserId());
            ps.setLong(2, event.getTimestamp());
            ps.setString(3, event.getEventType().name());
            ps.setString(4, event.getOperation().name());
            ps.setInt(5, event.getEntityId());
            return ps;
        }, holder);
        event.setEventId(holder.getKeyAs(Integer.class));
        log.info("Событие с идентификатором {} создано", event.getEventId());
        return event;
    }

    private Event mapRowToEvent(ResultSet resultSet, int rowNum) throws SQLException {
        return Event.builder()
                .eventId(resultSet.getInt("event_id"))
                .userId(resultSet.getInt("user_id"))
                .timestamp(resultSet.getLong("timestamps"))
                .eventType(EventEnum.valueOf(resultSet.getString("event_type")))
                .operation((OperationEnum.valueOf(resultSet.getString("operation_type"))))
                .entityId(resultSet.getInt("entity_id"))
                .build();

    }
}
