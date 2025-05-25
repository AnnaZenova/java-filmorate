package ru.yandex.practicum.filmorate.service.Director;

import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;

public interface DirectorService {
    List<Director> findAll();

    Director create(Director director);

    Director update(Director director);

    Director getDirectorById(Integer DirectorId);

    void delete(Integer id);
}
