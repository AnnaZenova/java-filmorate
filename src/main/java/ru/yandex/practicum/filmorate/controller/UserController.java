package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {
    private final Map<Integer, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> findAll() {
        log.info("Получен GET-запрос к эндпоинту: '/users' на получение users");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody @Valid User user) {
        log.info("Получен POST-запрос к эндпоинту: '/users' на добавление user");
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        user.setId(getNextId());
        users.put(user.getId(), user);
        return user;
    }

    private int getNextId() {
        int currentMaxId = users.keySet()
                .stream()
                .mapToInt(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    @PutMapping
    public User update(@RequestBody @Valid User user) {
        log.info("Получен PUT-запрос к эндпоинту: '/users' на обновление user");
        User oldUser = users.get(user.getId());
        if (!(user.getEmail().contains("@"))) {
            throw new NotFoundException("Электронная почта должна содержать символ @");
        }
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        oldUser = users.get(user.getId());
        oldUser.setName(user.getName());
        oldUser.setEmail(user.getEmail());
        oldUser.setBirthday(user.getBirthday());
        return user;
    }
}

