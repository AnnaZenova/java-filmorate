package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> findAll();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> showMostLikedFilms(int count);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(int id);
}
