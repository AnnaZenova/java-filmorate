package ru.yandex.practicum.filmorate.model;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
public class Film {
    private int id;
    @NotBlank(message = "Название фильма не может быть пустым")
    private String name;
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description;
    @NotNull
    private LocalDate releaseDate;
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private int duration;
    private final Set<Integer> likes = new HashSet<>();
    @NotNull
    private final Set<Genre> genres = new HashSet<>();
    @NotNull
    private Mpa mpa;

    public Film(int id, String name, String description, LocalDate releaseDate, Integer duration, Mpa mpa) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.mpa = mpa;
    }

    public void setLikes(Set<Integer> like) {
        likes.addAll(like);
    }

    public void setGenres(List<Genre> genresList) {
        genresList.sort(new Comparator<Genre>() {
            @Override
            public int compare(Genre o1, Genre o2) {
                return o1.getGenre_id() - o2.getGenre_id();
            }
        });
        genres.clear();
        genres.addAll(genresList);
    }
}
