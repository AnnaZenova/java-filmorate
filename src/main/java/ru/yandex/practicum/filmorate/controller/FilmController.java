package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
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
    final LocalDate BORDER_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping
    public Collection<Film> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/films' на получение фильмов");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody @Valid Film film) {
        log.info("Получен POST-запрос к эндпоинту: '/films' на добавление фильма");
        // проверяем выполнение необходимых условий
        if (film.getReleaseDate().isBefore(BORDER_DATE)) {
            throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
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
    public Film update(@RequestBody @Valid Film newFilm) {
        log.info("Получен PUT-запрос к эндпоинту: '/films' на обновление фильма");
        // проверяем необходимые условия
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getReleaseDate().isBefore(BORDER_DATE)) {
                throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
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
