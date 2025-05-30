package ru.yandex.practicum.filmorate.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class WrongDataException extends IllegalArgumentException {
    public WrongDataException(String message) {
        super(message);
    }
}

