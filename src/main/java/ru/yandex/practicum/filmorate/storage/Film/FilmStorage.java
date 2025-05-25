package ru.yandex.practicum.filmorate.storage.Film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAll();

    Film create(Film film);

    Film update(Film film);

    Film getFilmById(int filmId);

    void delete(int filmId);

    void putLikeToFilm(int filmId, int userId);

    List<Film> getFilmsByDirectorSortedByYear(Integer directorId);

    List<Film> getFilmsByDirectorSortedByLikes(Integer directorId);

    List<Film> getFilmsWithQueryAndDirectorName(String query);

    List<Film> getFilmsWithQueryAndFilmName(String query);

    List<Film> getFilmsWithQueryAndFilmPlusDirector(String query);
}
