package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
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
    private Set<Integer> friendsIds = new HashSet<>();
}
