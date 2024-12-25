package ru.yandex.practicum.filmorate;

import lombok.Builder;
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
	Film film;
	Film film1;
	Film film2;
	User user;
	User user1;
	User user2;

	@BeforeEach
	void shouldCreateBeforeTest() {
		film = new Film();
		film.setName("Пупсик");
		film.setReleaseDate(LocalDate.of(1700,11,12));
		film.setDescription("Description");
		film.setDuration(-100);

		film1 = new Film();

		film2 = new Film();
		film2.setName("Валидный");
		film2.setReleaseDate(LocalDate.of(2025,11,12));
		film2.setDescription("Description val");
		film2.setDuration(100);

		user = new User();
		user.setBirthday(LocalDate.of(2026,12,12));
		user.setLogin("Pupsik");
		user.setEmail("pupsik.ru");

		user1 = new User();

		user2 = new User();
		user2.setBirthday(LocalDate.of(2024,12,12));
		user2.setLogin("Pupsikensky");
		user2.setEmail("pupsik@.ru");

	}

	@Test
	@DisplayName("Создаем фильм")
	void shouldCreateMovie() {
		assertEquals(filmController.create(film2),film2);
	}

	@Test
	@DisplayName("Создаем пустой фильм")
	void shouldNotCreateEmptyMovie() {
		assertThrows(NotFoundException.class,() -> filmController.create(film2),"Валидация не пройдена" );
	}

	@Test
	@DisplayName("Создаем юзера")
	void shouldCreateUser() {
		assertEquals(userController.create(user2),user2);
	}

	@Test
	@DisplayName("Создаем пустого юзера")
	void shouldNotCreateEmptyUser() {
		assertThrows(NotFoundException.class,() -> userController.create(user1),"Валидация не пройдена" );
	}

	@Test
	@DisplayName("Создаем юзера c некорректными данными")
	void shouldNotCreateUserWithSuchData() {
		assertThrows(NotFoundException.class,() -> userController.create(user),"Валидация не пройдена" );
	}

	@Test
	@DisplayName("Создаем фильм c некорректными данными")
	void shouldNotCreatFilmWithSuchData() {
		assertThrows(WrongDataException.class,() -> filmController.create(film),"Валидация не пройдена" );
	}
}



