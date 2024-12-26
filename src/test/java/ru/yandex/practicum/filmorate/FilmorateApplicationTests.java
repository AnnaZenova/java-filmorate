package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDate;

@SpringBootTest
class FilmorateApplicationTests {
	FilmController filmController = new FilmController();
	UserController userController = new UserController();
	Film filmNotOk;
	Film filmEmpty;
	Film filmOk;
	User userNotOk;
	User userEmpty;
	User userOk;

	@BeforeEach
	void shouldCreateBeforeTest() {
		filmNotOk = new Film();
		filmNotOk.setName("Пупсик");
		filmNotOk.setReleaseDate(LocalDate.of(1700, 11, 12));
		filmNotOk.setDescription("Description");
		filmNotOk.setDuration(-100);

		filmEmpty = new Film();

		filmOk = new Film();
		filmOk.setName("Валидный");
		filmOk.setReleaseDate(LocalDate.of(2025, 11, 12));
		filmOk.setDescription("Description val");
		filmOk.setDuration(100);

		userNotOk = new User();
		userNotOk.setBirthday(LocalDate.of(2026, 12, 12));
		userNotOk.setLogin("Pupsik");
		userNotOk.setEmail("pupsik.ru");

		userEmpty = new User();

		userOk = new User();
		userOk.setBirthday(LocalDate.of(2024, 12, 12));
		userOk.setLogin("Pupsikensky");
		userOk.setEmail("pupsik@.ru");

	}

	@Test
	@DisplayName("Создаем фильм")
	void shouldCreateMovie() {
		assertEquals(filmController.create(filmOk), filmOk);
	}

	@Test
	@DisplayName("Создаем юзера")
	void shouldCreateUser() {
		assertEquals(userController.create(userOk), userOk);
	}

	@Test
	@DisplayName("Создаем юзера c некорректными данными")
	void shouldNotCreateUserWithSuchData() {
		assertThrows(NotFoundException.class, () -> userController.create(userNotOk), "Валидация не пройдена");
	}

	@Test
	@DisplayName("Создаем фильм c некорректными данными")
	void shouldNotCreatFilmWithSuchData() {
		assertThrows(WrongDataException.class, () -> filmController.create(filmNotOk), "Валидация не пройдена");
	}
}



