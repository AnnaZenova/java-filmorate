package ru.yandex.practicum.filmorate.service.Mpa;

import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaService {
    List<Mpa> getAllMpa();

    Mpa getMpa(int mpaId) throws NotFoundException;
}
