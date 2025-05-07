package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.service.Genre.GenreService;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;

@RestController
@RequestMapping("/genres")
@Slf4j
public class GenreController {
    private final GenreService genreService;

    @Autowired
    public GenreController(GenreService genreService) {
        this.genreService = genreService;
    }

    @GetMapping
    public List<Genre> getGenres() {
        log.info("Получен GET-запрос к эндпоинту: '/genres' на получение всех жанров");
        return genreService.getAllGenre();
    }


    @GetMapping("/{id}")
    public Genre getGenreById(@PathVariable Integer id) throws AccountNotFoundException {
        log.info("Получен GET-запрос к эндпоинту: '/genres' на получение жанра с ID={}", id);
        return genreService.getGenre(id);
    }
}