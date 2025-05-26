package ru.yandex.practicum.filmorate.service.Film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAll();

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> showMostLikedFilms(int count);

    Film create(Film film);

    Film update(Film newFilm);

    void delete(int id);

    Film getFilmById(int filmId);

    List<Film> getFilmsByDirectorSortedByYear(Integer directorId);

    List<Film> getFilmsByDirectorSortedByLikes(Integer directorId);

    List<Film> search(String query, String by);

    List<Film> findCommonFilms(int userId, int friendId);
}
