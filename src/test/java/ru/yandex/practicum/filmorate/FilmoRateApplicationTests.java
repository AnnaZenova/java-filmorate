package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.Film.FilmService;
import ru.yandex.practicum.filmorate.service.Film.FilmServiceImpl;
import ru.yandex.practicum.filmorate.service.User.UserService;
import ru.yandex.practicum.filmorate.service.User.UserServiceImpl;
import ru.yandex.practicum.filmorate.storage.Film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.Review.ReviewDbStorage;
import ru.yandex.practicum.filmorate.storage.User.UserDbStorage;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, ReviewDbStorage.class, FilmServiceImpl.class, UserServiceImpl.class })
class FilmoRateApplicationTests {
    private final UserDbStorage userStorage;
    private final FilmDbStorage filmStorage;
    private final ReviewDbStorage reviewStorage;
    private final FilmService filmService;
    private final UserService userService;
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
                .build();
        firstFilm.setMpa(new Mpa(4, "R"));
        firstFilm.setLikes(new HashSet<>());

        secondFilm = Film.builder()
                .name("Крик")
                .description("Good")
                .releaseDate(LocalDate.of(2009, 12, 10))
                .duration(162)
                .build();
        secondFilm.setMpa(new Mpa(5, "NC-17"));
        secondFilm.setLikes(new HashSet<>());

        thirdFilm = Film.builder()
                .name("Колобок")
                .description("Legendary")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .build();
        thirdFilm.setMpa(new Mpa(1, "G"));
        thirdFilm.setLikes(new HashSet<>());

        updateFilm = Film.builder()
                .id(firstFilm.getId())
                .name("UpdateName")
                .description("UpdateDescription")
                .releaseDate(LocalDate.of(1975, 11, 19))
                .duration(133)
                .build();
        updateFilm.setMpa(new Mpa(1, "G"));

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
    public void testGetPopularFilms() {

        firstUser = userStorage.create(firstUser);
        secondUser = userStorage.create(secondUser);
        thirdUser = userStorage.create(thirdUser);

        firstFilm = filmStorage.create(firstFilm);
        filmService.addLike(firstFilm.getId(), firstUser.getId());

        secondFilm = filmStorage.create(secondFilm);
        filmService.addLike(secondFilm.getId(), firstUser.getId());
        filmService.addLike(secondFilm.getId(), secondUser.getId());
        filmService.addLike(secondFilm.getId(), thirdUser.getId());

        thirdFilm = filmStorage.create(thirdFilm);
        filmService.addLike(thirdFilm.getId(), firstUser.getId());
        filmService.addLike(thirdFilm.getId(), secondUser.getId());

        List<Film> listFilms = filmService.showMostLikedFilms(5);

        assertThat(listFilms).hasSize(3);

        assertThat(Optional.of(listFilms.get(0)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Крик"));

        assertThat(Optional.of(listFilms.get(1)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Колобок"));

        assertThat(Optional.of(listFilms.get(2)))
                .hasValueSatisfying(film ->
                        AssertionsForClassTypes.assertThat(film)
                                .hasFieldOrPropertyWithValue("name", "Челюсти"));
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
}