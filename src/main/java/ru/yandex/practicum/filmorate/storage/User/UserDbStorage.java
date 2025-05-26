package ru.yandex.practicum.filmorate.storage.User;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.*;
import java.util.*;

@Slf4j
@Repository("UserDbStorage")
public class UserDbStorage implements UserStorage {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public UserDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<User> findAll() {
        String query = "SELECT * FROM users";
        return jdbcTemplate.query(query, this::mapRowToUser);
    }

    @Override
    public User create(User user) {
        SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");
        user.setId(simpleJdbcInsert.executeAndReturnKey(user.toMap()).intValue());
        log.info("Добавлен новый пользователь с ID={}", user.getId());
        return user;
    }

    @Override
    public User update(User user) {
        if (Integer.valueOf(user.getId()) == null) {
            throw new WrongDataException("Передан пустой аргумент!");
        } else {
            String sqlQuery = "UPDATE users SET " + "email = ?, login = ?, user_name = ?, birthday = ? " +
                    "WHERE user_id = ?";
            if (getUserById(user.getId()) == null) {
                throw new NotFoundException("Пользователь с ID=" + user.getId() + " не найден!");
            } else {
                jdbcTemplate.update(sqlQuery,
                        user.getEmail(),
                        user.getLogin(),
                        user.getName(),
                        user.getBirthday(),
                        user.getId());
                log.info("Пользователь с ID={} успешно обновлен", user.getId());
            }
        }
        return user;
    }

    @Override
    public List<User> getCommonFriends(int firstUserId, int secondUserId) {
        if (!userExists(firstUserId)) {
            throw new NotFoundException("Пользователь с ID=" + firstUserId + " не найден");
        }
        if (!userExists(secondUserId)) {
            throw new NotFoundException("Пользователь с ID=" + secondUserId + " не найден");
        }

        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select friend_id from friends_vs_users where user_id = ? ", firstUserId);
        Set<Integer> firstUserIds = new HashSet<>();
        while (userRows.next()) {
            firstUserIds.add(userRows.getInt(1));
        }
        Set<Integer> secondUserIds = new HashSet<>();
        while (userRows.next()) {
            secondUserIds.add(userRows.getInt(1));
        }

        boolean intersectionsArePresent = firstUserIds.retainAll(secondUserIds);
        if (!intersectionsArePresent) {
            throw new NotFoundException("Нет пересечений по друзьям");
        }
        List<User> commonFriends = new ArrayList<>();
        for (int i : firstUserIds) {
            commonFriends.add(getUserById(i));
        }
        return commonFriends;
    }

    @Override
    public User getUserById(int userId) {
        User user;
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("SELECT * FROM users WHERE user_id = ?", userId);
        if (userRows.first()) {
            user = new User(
                    userRows.getInt("user_id"),
                    userRows.getString("email"),
                    userRows.getString("login"),
                    userRows.getString("user_name"),
                    userRows.getDate("birthday").toLocalDate(),
                    null);
            return user;
        } else {
            return null;
        }
    }

    @Override
    public void deleteUser(int userId) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        jdbcTemplate.update(sql, userId);
    }

    @Override
    public void addFriend(int id, int friendId) {
        // Проверка существования пользователей в БД
        if (!userExists(id)) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователь с ID=" + friendId + " не найден");
        }

        // Проверка, не являются ли уже друзьями
        if (friendshipExists(id, friendId)) {
            throw new WrongDataException("Пользователи уже друзья");
        }
        String sql = "INSERT INTO friends_vs_users (user_id, friend_id)" + "VALUES (?, ?)";
        jdbcTemplate.update(sql, id, friendId);
        log.info("Пользователю с ID={} добавлен друг с Friend ID={}", id, friendId);
    }

    @Override
    public List<User> getFriends(int id) {
        if (!userExists(id)) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
        SqlRowSet userRows = jdbcTemplate.queryForRowSet("select friend_id from friends_vs_users where user_id = ? ", id);
        Set<Integer> friendsIds = new HashSet<>();
        while (userRows.next()) {
            friendsIds.add(userRows.getInt(1));
        }
        List<User> friends = new ArrayList<>();
        for (Integer fId : friendsIds) {
            friends.add(getUserById(fId));
        }
        log.info("Получены друзья пользователя с ID={}", id);
        return friends;
    }

    @Override
    public void deleteFriend(int id, int friendId) {
        if (!userExists(id)) {
            throw new NotFoundException("Пользователь с ID=" + id + " не найден");
        }
        if (!userExists(friendId)) {
            throw new NotFoundException("Пользователь с ID=" + friendId + " не найден");
        }
        String sql = "DELETE FROM friends_vs_users WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, id, friendId);
    }


    private User mapRowToUser(ResultSet resultSet, int rowNum) throws SQLException {
        return User.builder()
                .id(resultSet.getInt("user_id"))
                .email(resultSet.getString("email"))
                .login(resultSet.getString("login"))
                .name(resultSet.getString("user_name"))
                .birthday(resultSet.getDate("birthday").toLocalDate())
                .build();
    }

    private boolean userExists(int userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId);
        return count != null && count > 0;
    }

    private boolean friendshipExists(int userId, int friendId) {
        String sql = "SELECT COUNT(*) FROM friends_vs_users WHERE user_id = ? AND friend_id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, userId, friendId);
        return count != null && count > 0;
    }
}