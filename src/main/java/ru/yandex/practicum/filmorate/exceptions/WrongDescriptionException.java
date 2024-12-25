package ru.yandex.practicum.filmorate.exceptions;

public class WrongDescriptionException extends RuntimeException {
    public WrongDescriptionException(String message) {
        super(message);
    }
}