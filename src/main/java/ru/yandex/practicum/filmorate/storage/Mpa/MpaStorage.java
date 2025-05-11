package ru.yandex.practicum.filmorate.storage.Mpa;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaStorage {
    List<Mpa> getAllMpa();

    Mpa getMpa(Integer mpaId) throws NotFoundException;
}

