package ru.yandex.practicum.filmorate.service.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;

    @Override
    public void addFriend(int userId, int friendId) {
        userStorage.addFriend(userId, friendId);
        log.info("Добавили друга пользователю с ID: {}", userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        userStorage.deleteFriend(userId, friendId);
        log.info("Удалили друга у пользователя с ID: {}", userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        log.info("Получили друга с ID: {}", userId);
        return userStorage.getFriends(userId);
    }

    //вывод списка общих друзей
    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);

        // Если один из пользователей не найден, возвращаем пустой список
        if (firstUser == null || secondUser == null) {
            return Collections.emptyList();
        }

        // Получаем друзей для обоих пользователей
        Set<User> firstUserFriends = new HashSet<>(userStorage.getFriends(firstUserId));
        Set<User> secondUserFriends = new HashSet<>(userStorage.getFriends(secondUserId));

        // Находим пересечение множеств (общих друзей)
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

    public void deleteUser(int id) {
        User user = userStorage.getUserById(id);
        userStorage.deleteUser(id);
        log.info("Удален пользователь user: {}", user);
    }
}
