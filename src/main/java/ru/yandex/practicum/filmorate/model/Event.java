package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
public class Event {
    @JsonProperty("eventId")
    private int id;
    private int userId;
    private EventType eventType;
    private OperationType operation;
    private int entityId;
    @JsonIgnore
    private Timestamp timestamp;

    @JsonProperty("timestamp")
    public long getTimestampMillisecond() {
        return timestamp.getTime();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", userId);
        values.put("login", eventType);
        values.put("user_name", operation);
        values.put("birthday", entityId);
        values.put("timestamp", timestamp);
        return values;
    }
}