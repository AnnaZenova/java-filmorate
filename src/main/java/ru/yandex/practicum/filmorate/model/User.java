package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;

@Data
public class User {
    private int id;
    @NotEmpty(message = "Элекстронная почта не может быть пустой")
    private String email;
    @NotBlank(message = "Логин не может быть пустой")
    private String login;
    private String name;
    @PastOrPresent(message = "Дата не может быть в будущем")
    private LocalDate birthday;
}
