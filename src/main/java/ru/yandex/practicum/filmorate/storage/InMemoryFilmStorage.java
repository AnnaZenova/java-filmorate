package ru.yandex.practicum.filmorate.storage;

import jakarta.validation.Valid;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate BORDER_DATE = LocalDate.of(1895, 12, 28);

    @Override
    public Collection<Film> findAll() {
        return films.values();
    }

    @Override
    public Film create(@Valid Film film) {
        if (film.getReleaseDate() == null) {
            throw new NotFoundException("Дата релиза не может быть пустой");
        }
        if (film.getReleaseDate().isBefore(BORDER_DATE)) {
            throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
        }
        film.setId(getNextId());
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

    @Override
    public Film update(@Valid Film newFilm) {
        if (films.containsKey(newFilm.getId())) {
            Film oldFilm = films.get(newFilm.getId());
            if (newFilm.getReleaseDate() == null) {
                throw new NotFoundException("Дата релиза не может быть пустой");
            }
            if (newFilm.getReleaseDate().isBefore(BORDER_DATE)) {
                throw new WrongDataException("Дата релиза — не раньше 28 декабря 1895 года");
            }
            oldFilm.setDescription(newFilm.getDescription());
            oldFilm.setName(newFilm.getName());
            oldFilm.setDuration(newFilm.getDuration());
            oldFilm.setReleaseDate(newFilm.getReleaseDate());
            return oldFilm;
        }
        throw new NotFoundException("Пост с id = " + newFilm.getId() + " не найден");
    }

    @Override
    public Film getFilmById(int filmId) {
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
        return films.get(filmId);
    }

    @Override
    public void delete(int filmId) {
        if (Integer.valueOf(filmId) == null) {
            throw new NotFoundException("Передан пустой аргумент!");
        }
        if (!films.containsKey(filmId)) {
            throw new NotFoundException("Фильм с ID=" + filmId + " не найден!");
        }
    }
}


