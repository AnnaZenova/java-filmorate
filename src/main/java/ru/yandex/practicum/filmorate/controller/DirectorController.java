package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import lombok.extern.slf4j.Slf4j;
import ru.yandex.practicum.filmorate.service.Director.DirectorService;
import ru.yandex.practicum.filmorate.service.Film.FilmService;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/directors")
@Slf4j
public class DirectorController {
    private DirectorService directorService;

    public DirectorController(DirectorService directorService) {
        this.directorService = directorService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Director create(@RequestBody @Valid Director director) {
        log.info("Получен POST-запрос к эндпоинту: '/directors' на добавление режиссёра");
        return directorService.create(director);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public Director update(@RequestBody @Valid Director director) {
        log.info("Получен PUT-запрос к эндпоинту: '/directors' на обновление режиссёра");
        return directorService.update(director);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void delete(@PathVariable("id") Integer id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/directors' на удаление режиссёра");
        directorService.delete(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public Director getDirectorById(@PathVariable("id") Integer id) {
        log.info("Получен GET-запрос к эндпоинту: '/directors' на получение режиссёра по ID");
        return directorService.getDirectorById(id);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Director> getAllDirectors() {
        log.info("Получен GET-запрос к эндпоинту: '/directors' на получение списка всех режиссёров");
        return directorService.findAll();
    }
}
