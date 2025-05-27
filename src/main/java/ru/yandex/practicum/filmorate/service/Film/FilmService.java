package ru.yandex.practicum.filmorate.service.Film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> showMostLikedFilms(int count, Integer genreId, Integer year);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(int id);

    Film getFilmById(int filmId);

    List<Film> findCommonFilms(int userId, int friendId);
}
