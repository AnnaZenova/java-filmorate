package ru.yandex.practicum.filmorate.service.Film;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
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

    @Override
    public void addLike(@Valid int filmId, @Valid int userId) {
        Film film = filmStorage.getFilmById(filmId);
        if (film != null) {
            if (userStorage.getUserById(userId) != null) {
                filmStorage.putLikeToFilm(filmId, userId);
                log.info("Добавлен лайк пользователя с id-" + userId + " к фильму " + filmStorage.getFilmById(filmId));
            } else {
                throw new NotFoundException("Пользователь c ID=" + userId + " не найден!");
            }
        } else {
            throw new NotFoundException("Фильм c ID=" + filmId + " не найден!");
        }
    }

    @Override
    public void deleteLike(@Valid int filmId, @Valid int userId) {
        filmStorage.deleteLike(filmId, userId);
    }

    @Override
    public List<Film> showMostLikedFilms(int count) {
        if (count <= 0) {
            throw new IllegalArgumentException("Count must be positive");
        }

        List<Film> allFilms = new ArrayList<>(filmStorage.findAll());

        // Сортируем по возрастанию лайков (как ожидает тест)
        allFilms.sort((f1, f2) -> {
            int likes1 = f1.getLikes() != null ? f1.getLikes().size() : 0;
            int likes2 = f2.getLikes() != null ? f2.getLikes().size() : 0;
            return Integer.compare(likes2, likes1); // Обратное сравнение
        });
        log.info("Возвращаем список наиболее популярных фильмов");
        return allFilms.subList(0, Math.min(count, allFilms.size()));
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
        // проверяем необходимые условия
        return filmStorage.update(newFilm);
    }

    @Override
    public void delete(@PathVariable int id) {
        filmStorage.delete(id);
    }

    public Film getFilmById(int filmId) {
        return filmStorage.getFilmById(filmId);
    }

    @Override
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
