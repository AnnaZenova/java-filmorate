package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.User.UserService;
import ru.yandex.practicum.filmorate.service.recommendation.RecommendationService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final RecommendationService recommendationService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Collection<User> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение users");
        return userService.findAll();
    }

    @GetMapping("/{id}/friends")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех друзей");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    @ResponseStatus(HttpStatus.OK)
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на добавление друга");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @ResponseBody
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@RequestBody @Valid User user) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        return userService.create(user);
    }

    @PutMapping
    @ResponseStatus(HttpStatus.OK)
    public User update(@RequestBody @Valid User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление user");
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}", id);
        userService.deleteUser(id);
    }

    @GetMapping("{id}/feed")
    @ResponseStatus(HttpStatus.OK)
    public Collection<Event> getUserFeed(@PathVariable("id") int userId) {
        log.info("GET request received: get feed of user \"{}\"", userId);
        return userService.getUserFeed(userId);
    }

    @GetMapping("/{id}/recommendations")
    @ResponseStatus(HttpStatus.OK)
    public List<Film> getRecommendationsFilm(@PathVariable("id") int id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение рекомендаций по фильмам");
        return recommendationService.getRecommendationsFilms(id);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public User getUserById(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение пользователя по id");
        return userService.getUserById(id);
    }
}

