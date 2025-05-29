package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class Mpa {
    @JsonProperty("id")
    private Integer mpaId;

    @JsonProperty("name")
    @NotBlank
    private String mpaName;
}
