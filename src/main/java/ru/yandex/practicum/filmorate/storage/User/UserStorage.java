package ru.yandex.practicum.filmorate.storage.User;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAll();

    User create(User user);

    User update(User user);

    User getUserById(int userId);

    void deleteUser(int user_id);

    void addFriend(int id, int friendId);

    List<User> getFriends(int id);

    void deleteFriend(int id, int friendId);

    List<User> getCommonFriends(int firstUserId, int secondUserId);
}
