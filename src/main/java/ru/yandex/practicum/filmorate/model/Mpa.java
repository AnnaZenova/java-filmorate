package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    @JsonProperty("id")
    private final Integer mpaId;
    @JsonProperty("name")
    private final String mpaName;

    public Mpa(Integer mpaId, String mpaName) {
        this.mpaId = mpaId;
        this.mpaName = mpaName;
    }
}
