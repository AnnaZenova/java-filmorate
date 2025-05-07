package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Mpa {
    @JsonProperty("id")
    private final Integer mpa_id;
    @JsonProperty("name")
    private final String mpa_name;

    public Mpa(Integer mpa_id, String mpa_name) {
        this.mpa_id = mpa_id;
        this.mpa_name = mpa_name;
    }
}
