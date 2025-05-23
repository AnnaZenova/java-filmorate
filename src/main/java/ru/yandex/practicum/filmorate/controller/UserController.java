package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.User.UserService;

import java.util.Collection;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(
            UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение users");
        return userService.findAll();
    }

    @GetMapping("/{id}/friends")
    public List<User> getFriends(@PathVariable int id) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех друзей");
        return userService.getFriends(id);
    }

    @GetMapping("/{id}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable int id, @PathVariable int otherId) {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение всех общих друзей");
        return userService.getCommonFriends(id, otherId);
    }

    @PutMapping("/{id}/friends/{friendId}")
    public void addFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на добавление друга");
        userService.addFriend(id, friendId);
    }

    @DeleteMapping("/{id}/friends/{friendId}")
    public void deleteFriend(@PathVariable int id, @PathVariable int friendId) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление друга");
        userService.deleteFriend(id, friendId);
    }

    @ResponseBody
    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление пользователя");
        return userService.create(user);
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление user");
        return userService.update(user);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable int id) {
        log.info("Получен DELETE-запрос к эндпоинту: '/users' на удаление пользователя с ID={}", id);
        userService.deleteUser(id);
    }
}

