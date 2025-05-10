package ru.yandex.practicum.filmorate.storage.Genre;

import ru.yandex.practicum.filmorate.model.Genre;
import java.util.List;


public interface GenreStorage {
    List<Genre> getAllGenre();

    Genre getGenreById(Integer id);
}