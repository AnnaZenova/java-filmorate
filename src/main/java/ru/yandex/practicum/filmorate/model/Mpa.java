package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Mpa {
    @JsonProperty("id")
    private final Integer mpaId;
    @JsonProperty("name")
    private final String mpaName;
}
