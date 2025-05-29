package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;


@Data
@Builder
@AllArgsConstructor
public class Genre {
    @JsonProperty("id")
    private Integer genreId;

    @JsonProperty("name")
    @NotBlank
    private String genreName;
}

