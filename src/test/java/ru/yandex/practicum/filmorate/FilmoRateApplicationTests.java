package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;

import ru.yandex.practicum.filmorate.service.Director.DirectorService;
import ru.yandex.practicum.filmorate.service.Director.DirectorServiceImpI;
import ru.yandex.practicum.filmorate.service.Film.FilmService;
import ru.yandex.practicum.filmorate.service.Film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.Review.ReviewService;
import ru.yandex.practicum.filmorate.service.Review.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.service.User.UserService;
import ru.yandex.practicum.filmorate.service.User.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.Director.DirectorDbStorage;
import ru.yandex.practicum.filmorate.storage.Event.EventDbStorage;
import ru.yandex.practicum.filmorate.storage.Event.EventMapper;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.Genre.GenreDbStorage;
import ru.yandex.practicum.filmorate.storage.Mpa.MpaDbStorage;
import ru.yandex.practicum.filmorate.storage.Review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.User.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({
        UserDbStorage.class,
        FilmDbStorage.class,
        ReviewDbStorage.class,
        DirectorDbStorage.class,
        EventDbStorage.class,
        MpaDbStorage.class,
        GenreDbStorage.class,
        FilmServiceImpl.class,
        UserServiceImpl.class,
        ReviewServiceImpl.class,
        EventMapper.class,
        DirectorServiceImpI.class
})
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final ReviewDbStorage reviewStorage;
    private final DirectorDbStorage directorStorage;
    private final EventDbStorage eventStorage;
    private final MpaDbStorage mpaStorage;
    private final GenreDbStorage genreStorage;
    private final FilmService filmService;
    private final UserService userService;
    private final ReviewService reviewService;
    private final DirectorService directorService;

    private User firstUser;
    private User secondUser;
    private User thirdUser;
    private User updateUser;
    private Film firstFilm;
    private Film secondFilm;
    private Film thirdFilm;
    private Film updateFilm;
    private Review firstReview;
    private Review secondReview;
    private Review updatedReview;
    private Director director;
    private Event event;

    @BeforeEach
    public void beforeEach() {
        firstUser = User.builder()
                .name("Ivan")
                .login("First")
                .email("practicum1@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        updateUser = User.builder()
                .id(firstUser.getId())
                .name("UpdateIvan")
                .login("First")
                .email("practicum11@mail.ru")
                .birthday(LocalDate.of(1980, 12, 23))
                .build();

        secondUser = User.builder()
                .name("Anton")
                .login("Second")
                .email("practicum2@mail.ru")
                .birthday(LocalDate.of(1980, 12, 24))
                .build();

        thirdUser = User.builder()
                .name("Jenny")
                .login("Third")
                .email("practicum3@mail.ru")
                .birthday(LocalDate.of(1980, 12, 25))
                .build();

        firstFilm = Film.builder()
                .name("Челюсти")
                .description("Not so bad.")
                .releaseDate(LocalDate.of(1961, 10, 5))
                .duration(114)
                .mpa(new Mpa(4, "R"))
                .build();
        firstFilm.setGenres(new ArrayList<>(List.of(new Genre(1, "Комедия"))));
        firstFilm.setDirectors(new ArrayList<>());

        secondFilm = Film.builder()
                .name("Крик")
                .description("Good")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(162)
                .mpa(new Mpa(5, "NC-17"))
                .build();
        firstFilm.setGenres(new ArrayList<>(List.of(new Genre(2, "Драма"))));
        secondFilm.setDirectors(new ArrayList<>());

        thirdFilm = Film.builder()
                .name("Колобок")
                .description("Legendary")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .mpa(new Mpa(1, "G"))
                .build();
        thirdFilm.setGenres(new ArrayList<>());
        thirdFilm.setDirectors(new ArrayList<>());

        updateFilm = Film.builder()
                .id(firstFilm.getId())
                .name("UpdateName")
                .description("UpdateDescription")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .mpa(new Mpa(1, "G"))
                .build();
        updateFilm.setGenres(new ArrayList<>());
        updateFilm.setDirectors(new ArrayList<>());

        firstReview = Review.builder()
                .content("First review content")
                .isPositive(true)
                .userId(firstUser.getId())
                .filmId(firstFilm.getId())
                .useful(0)
                .build();

        secondReview = Review.builder()
                .content("Second review content")
                .isPositive(false)
                .userId(secondUser.getId())
                .filmId(secondFilm.getId())
                .useful(0)
                .build();

        updatedReview = Review.builder()
                .content("Updated review content")
                .isPositive(false)
                .userId(firstUser.getId())
                .filmId(firstFilm.getId())
                .useful(0)
                .build();

        director = Director.builder()
                .directorName("Test Director")
                .build();

    }

    @Test
    public void testUpdateUser() {
        firstUser = userStorage.create(firstUser);
        updateUser = userStorage.create(updateUser);
        Optional<User> testUpdateUser = Optional.ofNullable(userStorage.update(updateUser));
        assertThat(testUpdateUser)
                .hasValueSatisfying(user -> assertThat(user)
                        .hasFieldOrPropertyWithValue("name", "UpdateIvan")
                );
    }


    @Test
    public void testCreateUserAndGetUserById() {
        firstUser = userStorage.create(firstUser);
        Optional<User> userOptional = Optional.ofNullable(userStorage.getUserById(firstUser.getId()));
        assertThat(userOptional)
                .hasValueSatisfying(user ->
                        assertThat(user)
                                .hasFieldOrPropertyWithValue("id", firstUser.getId())
                                .hasFieldOrPropertyWithValue("name", "Ivan"));
    }


    @Test
    public void testGetUsers() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        List<User> listUsers = userStorage.findAll();
        assertThat(listUsers).contains(firstUser);
        assertThat(listUsers).contains(secondUser);
    }

    @Test
    public void deleteUser() {
        firstUser = userStorage.create(firstUser);
        userStorage.deleteUser(firstUser.getId());
        List<User> listUsers = userStorage.findAll();
        assertThat(listUsers).hasSize(0);
    }

    @Test
    public void testCreateFilmAndGetFilmById() {
        firstFilm = filmStorage.create(firstFilm);
        Optional<Film> filmOptional = Optional.ofNullable(filmStorage.getFilmById(firstFilm.getId()));
        assertThat(filmOptional)
                .hasValueSatisfying(film -> assertThat(film)
                        .hasFieldOrPropertyWithValue("id", firstFilm.getId())
                        .hasFieldOrPropertyWithValue("name", "Челюсти")
                );
    }

    @Test
    public void testGetFilms() {
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);
        thirdFilm = filmStorage.create(thirdFilm);
        List<Film> listFilms = filmStorage.findAll();
        assertThat(listFilms).contains(firstFilm);
        assertThat(listFilms).contains(secondFilm);
        assertThat(listFilms).contains(thirdFilm);
    }

    @Test
    public void testUpdateFilm() {
        firstFilm = filmStorage.create(firstFilm);
        updateFilm = filmStorage.create(updateFilm);
        Optional<Film> testUpdateFilm = Optional.ofNullable(filmStorage.update(updateFilm));
        assertThat(testUpdateFilm)
                .hasValueSatisfying(film ->
                        assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "UpdateName")
                                .hasFieldOrPropertyWithValue("description", "UpdateDescription")
                );
    }

    @Test
    public void testAddLike() {
        firstUser = userStorage.create(firstUser);
        firstFilm = filmStorage.create(firstFilm);
        filmService.addLike(firstFilm.getId(), firstUser.getId());
        firstFilm = filmStorage.getFilmById(firstFilm.getId());
        assertThat(firstFilm.getLikes()).hasSize(1);
        assertThat(firstFilm.getLikes()).contains(firstUser.getId());
    }


    @Test
    public void testAddFriend() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        assertThat(userStorage.getFriends(firstUser.getId())).hasSize(1);
        assertThat(userStorage.getFriends(firstUser.getId())).contains(secondUser);
    }

    @Test
    public void testDeleteFriend() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        userStorage.addFriend(firstUser.getId(), thirdUser.getId());
        userStorage.deleteFriend(firstUser.getId(), secondUser.getId());
        assertThat(userStorage.getFriends(firstUser.getId())).hasSize(1);
        assertThat(userStorage.getFriends(firstUser.getId())).contains(thirdUser);
    }

    @Test
    public void testGetFriends() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        userStorage.addFriend(firstUser.getId(), thirdUser.getId());
        assertThat(userStorage.getFriends(firstUser.getId())).hasSize(2);
        assertThat(userStorage.getFriends(firstUser.getId())).contains(secondUser, thirdUser);
    }

    @Test
    public void testGetCommonFriends() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);
        userStorage.addFriend(firstUser.getId(), secondUser.getId());
        userStorage.addFriend(firstUser.getId(), thirdUser.getId());
        userStorage.addFriend(secondUser.getId(), firstUser.getId());
        userStorage.addFriend(secondUser.getId(), thirdUser.getId());
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId())).hasSize(1);
        assertThat(userService.getCommonFriends(firstUser.getId(), secondUser.getId()))
                .contains(thirdUser);
    }

    @Test
    public void testCreateAndGetReviewById() {
        // Сначала создаем пользователя и фильм, так как отзыв зависит от них
        firstUser = userStorage.create(firstUser);
        firstFilm = filmStorage.create(firstFilm);
        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review createdReview = reviewStorage.create(firstReview);
        Optional<Review> reviewOptional = Optional.ofNullable(reviewStorage.getReviewById(createdReview.getReviewId()));

        assertThat(reviewOptional)
                .hasValueSatisfying(review -> assertThat(review)
                        .hasFieldOrPropertyWithValue("reviewId", createdReview.getReviewId())
                        .hasFieldOrPropertyWithValue("content", "First review content")
                        .hasFieldOrPropertyWithValue("isPositive", true)
                );
    }

    @Test
    public void testUpdateReview() {
        firstUser = userStorage.create(firstUser);
        firstFilm = filmStorage.create(firstFilm);
        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review createdReview = reviewStorage.create(firstReview);
        updatedReview.setReviewId(createdReview.getReviewId());
        updatedReview.setUserId(firstUser.getId());
        updatedReview.setFilmId(firstFilm.getId());

        Optional<Review> testUpdateReview = Optional.ofNullable(reviewStorage.update(updatedReview));

        assertThat(testUpdateReview)
                .hasValueSatisfying(review ->
                        assertThat(review)
                                .hasFieldOrPropertyWithValue("content", "Updated review content")
                                .hasFieldOrPropertyWithValue("isPositive", false)
                );
    }

    @Test
    public void testDeleteReview() {
        firstUser = userStorage.create(firstUser);
        firstFilm = filmStorage.create(firstFilm);
        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review createdReview = reviewStorage.create(firstReview);
        reviewStorage.deleteById(createdReview.getReviewId());

        assertThrows(NotFoundException.class, () -> reviewStorage.getReviewById(createdReview.getReviewId()));
    }

    @Test
    public void testGetReviewsByFilmId() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());
        secondReview.setUserId(secondUser.getId());
        secondReview.setFilmId(secondFilm.getId());

        Review review1 = reviewStorage.create(firstReview);
        Review review2 = reviewStorage.create(secondReview);

        List<Review> reviews = reviewStorage.getReviewByFilmId(firstFilm.getId(), 10);

        assertThat(reviews)
                .hasSize(1)
                .containsExactly(review1);
    }

    @Test
    public void testGetAllReviewsWithLimit() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);
        secondFilm = filmStorage.create(secondFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());
        secondReview.setUserId(secondUser.getId());
        secondReview.setFilmId(secondFilm.getId());

        Review review1 = reviewStorage.create(firstReview);
        Review review2 = reviewStorage.create(secondReview);

        List<Review> reviews = reviewStorage.getReviewLimit(1);

        assertThat(reviews).hasSize(1);
    }

    @Test
    public void testAddLikeToReview() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review review = reviewStorage.create(firstReview);
        reviewStorage.userLikesReview(review.getReviewId(), secondUser.getId());
        Review updatedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(updatedReview.getUseful()).isEqualTo(1);
    }

    @Test
    public void testAddDislikeToReview() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review review = reviewStorage.create(firstReview);
        reviewStorage.userDislikesReview(review.getReviewId(), secondUser.getId());
        Review updatedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(updatedReview.getUseful()).isEqualTo(-1);
    }

    @Test
    public void testDeleteLikeFromReview() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review review = reviewStorage.create(firstReview);
        reviewStorage.userLikesReview(review.getReviewId(), secondUser.getId());
        reviewStorage.deleteUsersLike(review.getReviewId(), secondUser.getId());
        Review updatedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(updatedReview.getUseful()).isEqualTo(0);
    }

    @Test
    public void testConvertDislikeToLike() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());

        Review review = reviewStorage.create(firstReview);
        reviewStorage.userDislikesReview(review.getReviewId(), secondUser.getId());
        reviewStorage.userLikesReview(review.getReviewId(), secondUser.getId());
        Review updatedReview = reviewStorage.getReviewById(review.getReviewId());

        assertThat(updatedReview.getUseful()).isEqualTo(1);
    }

    @Test
    public void testDirectorOperations() {
        Director created = directorStorage.create(director);
        Optional<Director> testDirector = Optional.ofNullable(directorStorage.getDirectorById(created.getDirectorId()));

        assertThat(testDirector)
                .hasValueSatisfying(d -> assertThat(d)
                        .hasFieldOrPropertyWithValue("directorName", "Test Director"));

        Director updated = Director.builder()
                .directorId(created.getDirectorId())
                .directorName("Updated Director")
                .build();

        directorStorage.update(updated);
        assertThat(directorStorage.getDirectorById(created.getDirectorId()).getDirectorName())
                .isEqualTo("Updated Director");

        List<Director> directors = directorStorage.findAll();
        assertThat(directors).hasSize(1);

        directorStorage.delete(created.getDirectorId());
        assertThrows(NotFoundException.class, () -> directorStorage.getDirectorById(created.getDirectorId()));
    }

    @Test
    public void testMpaOperations() {
        List<Mpa> allMpa = mpaStorage.getAllMpa();
        assertThat(allMpa).hasSize(5);

        Mpa mpa = mpaStorage.getMpa(1);
        assertThat(mpa.getMpaName()).isEqualTo("G");

        assertThrows(NotFoundException.class, () -> mpaStorage.getMpa(0));
        assertThrows(NotFoundException.class, () -> mpaStorage.getMpa(6));
    }

    @Test
    public void testGenreOperations() {
        List<Genre> allGenres = genreStorage.getAllGenre();
        assertThat(allGenres).hasSize(6);

        Genre genre = genreStorage.getGenreById(1);
        assertThat(genre.getGenreName()).isEqualTo("Комедия");

        assertThrows(NotFoundException.class, () -> genreStorage.getGenreById(0));
        assertThrows(NotFoundException.class, () -> genreStorage.getGenreById(7));
    }

    @Test
    public void testFilmWithDirectors() {
        Director dir = directorStorage.create(director);
        firstFilm.getDirectors().add(dir);

        firstFilm = filmStorage.create(firstFilm);
        Film retrieved = filmStorage.getFilmById(firstFilm.getId());

        assertThat(retrieved.getDirectors()).hasSize(1);
        assertThat(retrieved.getDirectors().iterator().next().getDirectorName())
                .isEqualTo("Test Director");
    }

    @Test
    public void testReviewUsefulCalculation() {
        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        firstFilm = filmStorage.create(firstFilm);

        firstReview.setUserId(firstUser.getId());
        firstReview.setFilmId(firstFilm.getId());
        Review review = reviewStorage.create(firstReview);

        reviewService.userLikesReview(review.getReviewId(), secondUser.getId());
        Review likedReview = reviewStorage.getReviewById(review.getReviewId());
        assertThat(likedReview.getUseful()).isEqualTo(1);

        reviewService.userDislikesReview(review.getReviewId(), secondUser.getId());
        Review dislikedReview = reviewStorage.getReviewById(review.getReviewId());
        assertThat(dislikedReview.getUseful()).isEqualTo(-1);
    }
}