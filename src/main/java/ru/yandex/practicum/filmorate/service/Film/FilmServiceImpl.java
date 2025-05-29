package ru.yandex.practicum.filmorate.service.Film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
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
    public void addLike(@Valid int filmId, @Valid int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                filmStorage.putLikeToFilm(filmId, userId);
                eventStorage.createEvent(userId, EventType.LIKE, OperationType.ADD, filmId);
                log.info("Добавлен лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(filmId));
            } else {
                log.info("Выброшено исключение NotFoundException");
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            log.info("Выброшено исключение NotFoundException");
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    @Override
    public void deleteLike(@Valid int filmId, @Valid int userId) {
        filmStorage.deleteLike(filmId, userId);
        eventStorage.createEvent(userId, EventType.LIKE, OperationType.REMOVE, filmId);
        log.info("Добавлен лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(filmId));
    }

    @Override
    public List<Film> showMostLikedFilms(int count, Integer genreId, Integer year) {
        if (count <= 0) {
            log.info("Выброшено исключение IllegalArgumentException");
            throw new IllegalArgumentException("Count must be positive");
        }
        log.info("Возвращен список наиболее понравившихся фильмов!");
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
        log.info("Возвращен список всех фильмов!");
        return filmStorage.findAll();
    }

    @Override
    public Film create(@RequestBody @Valid Film film) {
        log.info("Создан фильм с id {}", film.getId());
        return filmStorage.create(film);
    }

    @Override
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Обновлен фильм с id {}", newFilm.getId());
        return filmStorage.update(newFilm);
    }

    @Override
    public void delete(@PathVariable int id) {
        log.info("Удален фильм с id {}", id);
        filmStorage.delete(id);
    }

    @Override
    public Film getFilmById(int filmId) {
        log.info("Передача фильма с id {}", filmId);
        return filmStorage.getFilmById(filmId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByYear(Integer directorId) {
        log.info("Передача отсортированного по годам списка фильмов с id режиссёра {}", directorId);
        return filmStorage.getFilmsByDirectorSortedByYear(directorId);
    }

    @Override
    public List<Film> getFilmsByDirectorSortedByLikes(Integer directorId) {
        log.info("Передача отсортированного по лайкам списка фильмов с id режиссёра {}", directorId);
        return filmStorage.getFilmsByDirectorSortedByLikes(directorId);
    }

    @Override
    public List<Film> search(String query, String by) {
        query = "%" + query + "%";
        String[] bySplited = by.split(",");
        log.info("Передача списка фильмов по субстроке с сортировкой по {}", by);
        if (bySplited.length == 1 && "director".equals(bySplited[0])) {
            return filmStorage.getFilmsWithQueryAndDirectorName(query);
        } else if (bySplited.length == 1 && "title".equals(bySplited[0])) {
            return filmStorage.getFilmsWithQueryAndFilmName(query);
        } else if (bySplited.length == 2 && (
                ("title".equals(bySplited[0]) && "director".equals(bySplited[1])) ||
                        ("director".equals(bySplited[0]) && "title".equals(bySplited[1])))) {
            return filmStorage.getFilmsWithQueryAndFilmPlusDirector(query);
        } else {
            return new ArrayList<>();
        }
    }

    public List<Film> findCommonFilms(int userId, int friendId) {
        // Проверяем существование пользователей
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException(String.format("Пользователь с ID = %d не найден", userId));
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException(String.format("Пользователь с ID = %d не найден", friendId));
        }
        log.info("Поиск общих фильмов у пользователей с id = {} и id = {}", userId, friendId);
        return filmStorage.findCommonFilms(userId, friendId);
    }
}
