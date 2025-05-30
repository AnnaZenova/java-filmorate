package ru.yandex.practicum.filmorate.service.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        eventStorage.createEvent(userId, EventType.FRIEND, OperationType.ADD, friendId);
        log.info("Добавили друга пользователю с ID: {}", userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        eventStorage.createEvent(userId, EventType.FRIEND, OperationType.REMOVE, friendId);
        log.info("Удалили друга у пользователя с ID: {}", userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        log.info("Получили друга с ID: {}", userId);
        return userStorage.getFriends(userId);
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);

        if (firstUser == null || secondUser == null) {
            return Collections.emptyList();
        }

        Set<User> firstUserFriends = new HashSet<>(userStorage.getFriends(firstUserId));
        Set<User> secondUserFriends = new HashSet<>(userStorage.getFriends(secondUserId));

        firstUserFriends.retainAll(secondUserFriends);
        log.info("Вернули список всех пользователей общих друзей");
        return new ArrayList<>(firstUserFriends);
    }

    @Override
    public Collection<User> findAll() {
        log.info("Вернули список всех пользователей");
        return userStorage.findAll();
    }

    @Override
    public User create(@Valid User user) {
        log.info("Создан/добавлен пользователь user: {}", user);
        return userStorage.create(user);
    }

    @Override
    public User update(@Valid User user) {
        log.info("Обновлен пользователь user: {}", user);
        return userStorage.update(user);
    }

    @Override
    public void deleteUser(int id) {
        User user = userStorage.getUserById(id);
        if (user != null) {
            userStorage.deleteUser(id);
        } else {
            throw new NotFoundException("Нет такого юзера !");
        }
        log.info("Удален пользователь user: {}", user);
    }

    @Override
    public Collection<Event> getUserFeed(int userId) {
        getUserById(userId);
        log.info("Возвращен список действие пользователя с id = {}", userId);
        return eventStorage.getEventByUserId(userId);
    }

    @Override
    public User getUserById(int userId) {
        log.info("Возвращен пользователь с id = {}", userId);
        return userStorage.getUserById(userId);
    }
}
