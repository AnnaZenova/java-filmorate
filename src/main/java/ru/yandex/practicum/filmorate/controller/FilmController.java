package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.Film.FilmService;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {
    private final FilmService filmService;
    private final FilmStorage filmStorage;

    @Autowired
    public FilmController(@Qualifier("FilmDbStorage") FilmStorage filmStorage, FilmService filmService) {
        this.filmStorage = filmStorage;
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильмов");
        return filmStorage.findAll();
    }

    @GetMapping("/popular")
    public List<Film> showMostLikedFilms(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение самого отлайканного фильма");
        return filmService.showMostLikedFilms(count);
    }

    @ResponseBody
    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма");
        film = filmStorage.create(film);
        return film;
    }

    @PutMapping
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма");
        newFilm = filmStorage.update(newFilm);
        return newFilm;
    }

    @PutMapping("/{id}/like/{userId}")
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление лайка фильма с ID={}", id);
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", id);
        filmStorage.delete(id);
    }

    @GetMapping("/{id}")
    public Film getFilmById(@PathVariable int id) {
        return filmStorage.getFilmById(id);
    }
}

