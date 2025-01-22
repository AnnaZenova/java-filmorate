package ru.yandex.practicum.filmorate.service;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.*;

@Service
public class UserService {
    private UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public void addFriend(@Valid int userId, @Valid int friendId) {
        User userToAddFriend = userStorage.getUserById(userId);
        System.out.println(userToAddFriend);
        User friendToAddUser = userStorage.getUserById(friendId);
        userToAddFriend.getFriendsIds().add(friendId);
        friendToAddUser.getFriendsIds().add(userId);
    }

    public void deleteFriend(@Valid int userId, @Valid int friendId) {
        User user = userStorage.getUserById(userId);
        user.getFriendsIds().remove(friendId);
        User friend = userStorage.getUserById(friendId);
        friend.getFriendsIds().remove(userId);
    }

    public List<User> getFriends(@Valid int userId) {
        User user = userStorage.getUserById(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : user.getFriendsIds()) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    //вывод списка общих друзей
    public List<User> getCommonFriends(@Valid int firstUserId, @Valid int secondUserId) {
        User firstUser = userStorage.getUserById(firstUserId);
        User secondUser = userStorage.getUserById(secondUserId);
        Set<Integer> intersection = firstUser.getFriendsIds();
        boolean intersectionsArePresent = intersection.retainAll(secondUser.getFriendsIds());
        if (!intersectionsArePresent) {
            throw new NotFoundException("Нет пересечений по друзьям");
        }
        List<User> commonFriends = new ArrayList<>();
        for (int i : intersection) {
            commonFriends.add(userStorage.getUserById(i));
        }
        return commonFriends;
    }
}
