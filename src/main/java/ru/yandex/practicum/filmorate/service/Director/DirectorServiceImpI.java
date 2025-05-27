package ru.yandex.practicum.filmorate.service.Director;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.Director.DirectorStorage;

import java.util.List;

@Service
@Slf4j
public class DirectorServiceImpI implements DirectorService {

    private final DirectorStorage directorStorage;

    public DirectorServiceImpI(DirectorStorage directorStorage) {
        this.directorStorage = directorStorage;
    }

    @Override
    public List<Director> findAll() {
        log.info("Вернули список всех режиссёров");
        return directorStorage.findAll();
    }

    @Override
    public Director create(Director director) {
        log.info("Добавлен режиссёр {}", director.getDirectorName());
        return directorStorage.create(director);
    }

    @Override
    public Director update(Director director) {
        log.info("Информация о режиссёре с ID {} обновлена", director.getDirectorId());
        return directorStorage.update(director);
    }

    @Override
    public Director getDirectorById(Integer directorId) {
        log.info("Информация о режиссёре с ID {}:", directorId);
        return directorStorage.getDirectorById(directorId);
    }

    @Override
    public void delete(Integer directorId) {
        log.info("Сведения о режиссёре с ID {} удалены", directorId);
        directorStorage.delete(directorId);
    }
}
