package ru.yandex.practicum.filmorate.service.User;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.*;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Qualifier("UserDbStorage")
    private final UserStorage userStorage;

    @Override
    public void addFriend(int userId, int friendId) {
        User userToAddFriend = userStorage.getUserById(userId);
        User friendToAddUser = userStorage.getUserById(friendId);
        userToAddFriend.getFriendsIds().add(friendId);
        friendToAddUser.getFriendsIds().add(userId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().remove(friendId);
        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().remove(userId);
    }

    @Override
    public List<User> getFriends(int userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : user.getFriendsIds()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
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

        return new ArrayList<>(firstUserFriends);
    }

    @Override
    public Collection<User> findAll() {
        return userStorage.findAll();
    }

    @Override
    public User create(@Valid User user) {
        user = userStorage.create(user);
        return user;
    }

    @Override
    public User update(@Valid User user) {
        user = userStorage.update(user);
        return user;
    }
}
