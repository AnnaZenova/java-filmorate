package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Builder
public class User {
    private int id;
    @NotEmpty(message = "Элекстронная почта не может быть пустой")
    @Email
    private String email;
    @NotBlank(message = "Логин не может быть пустой")
    @Pattern(regexp = "\\S*", message = "Логин не может содержать пробелы")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate birthday;
    private Set<Integer> friendsIds;

    public User(int id, String email, String login, String name, LocalDate birthday, Set<Integer> friendsIds) {
        this.id = id;
        this.email = email;
        this.login = login;
        this.name = name;
        if ((name == null) || (name.isEmpty()) || (name.isBlank())) {
            this.name = login;
        }
        this.birthday = birthday;
        this.friendsIds = friendsIds;
        if (friendsIds == null) {
            this.friendsIds = new HashSet<>();
        }
    }

    public Map<String, Object> toMap() {
        Map<String, Object> values = new HashMap<>();
        values.put("email", email);
        values.put("login", login);
        values.put("user_name", name);
        values.put("birthday", birthday);
        return values;
    }
}
