package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class Genre {
    @JsonProperty("id")
    private final Integer genreId;
    @JsonProperty("name")
    private final String genreName;
}

