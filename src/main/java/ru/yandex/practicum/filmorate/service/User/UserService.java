package ru.yandex.practicum.filmorate.service.User;

import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;

public interface UserService {
    void addFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> getFriends(int userId);

    List<User> getCommonFriends(int firstUserId, int secondUserId);

    Collection<User> findAll();

    User create(User user);

    User update(User user);

    void deleteUser(int id);
}
