package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

    @Data
    @Builder
    public class Genre {
        @JsonProperty("id")
        private final Integer genreId;
        @JsonProperty("name")
        private final String genreName;

        public Genre(Integer genreId, String genreName) {
            this.genreId = genreId;
            this.genreName = genreName;
        }
    }

