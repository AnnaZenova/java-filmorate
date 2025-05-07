package ru.yandex.practicum.filmorate.storage.Genre;

import ru.yandex.practicum.filmorate.model.Genre;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;


public interface GenreStorage {
    List<Genre> getAllGenre();

    Genre getGenreById(Integer id) throws AccountNotFoundException;
}