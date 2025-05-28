package ru.yandex.practicum.filmorate.service.Film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.ArrayList;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor
public class FilmServiceImpl implements FilmService {

    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public void addLike(int filmId, int userId) {
        Film film = filmStorage.getFilmById(filmId);
        User user = userStorage.getUserById(userId);

        if (film == null) {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        if (user == null) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден!");
        }
        filmStorage.putLikeToFilm(filmId, userId);
        log.info("Пользователь ID={} поставил лайк фильму ID={}", userId, filmId);
        eventStorage.addEvent(userId, EventEnum.LIKE, OperationEnum.ADD, filmId);
    }

    @Override
    public void deleteLike(@Valid int filmId, @Valid int userId) {
        filmStorage.deleteLike(filmId, userId);
        eventStorage.addEvent(userId, EventEnum.LIKE, OperationEnum.REMOVE, filmId);
    }

    @Override
    public List<Film> showMostLikedFilms(int count, Integer genreId, Integer year) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }
        if (genreId != null && year != null) {
            return filmStorage.getPopularFilmsByGenreAndYear(count, genreId, year);
        } else if (genreId != null) {
            return filmStorage.getPopularFilmsByGenre(count, genreId);
        } else if (year != null) {
            return filmStorage.getPopularFilmsByYear(count, year);
        } else {
            return new ArrayList<>(filmStorage.getPopularFilms(count));
        }
    }

    @Override
    public List<Film> findAll() {
        return filmStorage.findAll();
    }

    @Override
    public Film create(@RequestBody @Valid Film film) {
        return filmStorage.create(film);
    }

    @Override
    public Film update(@RequestBody @Valid Film newFilm) {
        return filmStorage.update(newFilm);
    }

    @Override
    public void delete(@PathVariable int id) {
        filmStorage.delete(id);
    }

    @Override
    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(Integer directorId) {
        return filmStorage.getFilmsByDirectorSortedByYear(directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(Integer directorId) {
        return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
    }

    @Override
    public List<Film> search(String query, String by) {
        query = "%" + query + "%";
        String[] bySplited = by.split(",");
        if (bySplited.length == 1 && bySplited[0].equals("director")) {
            return filmStorage.getFilmsWithQueryAndDirectorName(query);
        } else if (bySplited.length == 1 && bySplited[0].equals("title")) {
            return filmStorage.getFilmsWithQueryAndFilmName(query);
        } else if (bySplited.length == 2 && (
                (bySplited[0].equals("title") && bySplited[1].equals("director")) ||
                        (bySplited[0].equals("director") && bySplited[1].equals("title")))) {
            return filmStorage.getFilmsWithQueryAndFilmPlusDirector(query);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Film> findCommonFilms(int userId, int friendId) {
        // Проверяем существование пользователей
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь с ID=" + userId + " не найден");
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Пользователь с ID=" + friendId + " не найден");
        }
        return filmStorage.findCommonFilms(userId, friendId);
    }
}
