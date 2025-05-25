package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.Film.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {
    private final FilmService filmService;

    @Autowired
    public FilmController(FilmService filmService) {
        this.filmService = filmService;
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<Film> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильмов");
        return filmService.findAll();
    }

    @GetMapping("/popular")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> showMostLikedFilms(@RequestParam(name = "count", defaultValue = "10") Integer count) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение самого отлайканного фильма");
        return filmService.showMostLikedFilms(count);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Film create(@RequestBody @Valid Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма");
        return filmService.create(film);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма");
        return filmService.update(newFilm);
    }

    @PutMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void addLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на добавление лайка");
        filmService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteLike(@PathVariable int id, @PathVariable int userId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление лайка фильма с ID={}", id);
        filmService.deleteLike(id, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/films' на удаление фильма с ID={}", id);
        filmService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Film getFilmById(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильмов c ID={}", id);
        return filmService.getFilmById(id);
    }

    @GetMapping("/director/{directorId}")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsByDirector(@PathVariable("directorId") int directorId,
                                         @RequestParam(defaultValue = "year") String sortBy) {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильмов режиссёра c ID={}", directorId);
        if (sortBy.equals("year")) {
            return filmService.getFilmsByDirectorSortedByYear(directorId);
        } else if (sortBy.equals("likes")) {
            return filmService.getFilmsByDirectorSortedByLikes(directorId);
        } else {
            throw new NotFoundException("Параметр sortBy должен быть 'year' или 'likes'");
        }
    }

    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getFilmsByDirector(@RequestParam(required = false) String query,
                                         @RequestParam(defaultValue = "title") String by) {
        log.info("Получен GET-запрос к эндпоинту: '/films/search' на получение фильмов по названию и режиссёру");
        return filmService.search(query, by);
    }
}

