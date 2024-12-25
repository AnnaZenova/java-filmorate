package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.exceptions.WrongDescriptionException;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/films")
@Slf4j
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    LocalDate borderDate = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма");
        // проверяем выполнение необходимых условий
        if (film.getName() == null || film.getName().isBlank()) {
            throw new NotFoundException("Название не может быть пустым");
        }
        if (film.getDescription().length() >= 200) {
            throw new WrongDescriptionException("Описание не должно превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(borderDate)) {
            throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        if (film.getDuration() < 0) {
            throw new WrongDataException("продолжительность фильма должна быть положительным числом");
        }
        // формируем дополнительные данные
        film.setId(getNextId());
        // сохраняем новую публикацию в памяти приложения
        films.put(film.getId(), film);
        return film;
    }

    private int getNextId() {
        int currentMaxId = films.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public Film update(@RequestBody Film newFilm) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма");
        // проверяем необходимые условия
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getName() == null || newFilm.getName().isBlank()) {
                throw new NotFoundException("Название не может быть пустым");
            }
            if (newFilm.getDescription().length() >= 200) {
                throw new WrongDescriptionException("Описание не должно превышать 200 символов");
            }
            if (newFilm.getReleaseDate().isBefore(borderDate)) {
                throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            if (newFilm.getDuration() < 0) {
                throw new WrongDataException("продолжительность фильма должна быть положительным числом");
            }
            // если публикация найдена и все условия соблюдены, обновляем её содержимое
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return oldFilm;
        }
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }
}
