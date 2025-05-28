package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;

import ru.yandex.practicum.filmorate.service.Film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.User.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.Review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.User.UserDbStorage;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, ReviewDbStorage.class, FilmServiceImpl.class, UserServiceImpl.class})
class FilmoRateApplicationTests{

}