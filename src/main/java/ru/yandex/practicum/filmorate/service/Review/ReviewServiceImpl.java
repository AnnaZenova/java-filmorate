package ru.yandex.practicum.filmorate.service.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.OperationType;
import ru.yandex.practicum.filmorate.storage.Event.EventStorage;
import ru.yandex.practicum.filmorate.storage.Film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.Review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.User.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {
    private final ReviewStorage reviewStorage;
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;
    private final EventStorage eventStorage;

    @Override
    public Review create(@RequestBody Review review) {
        review = reviewStorage.create(review);
        eventStorage.createEvent(review.getUserId(), EventType.REVIEW, OperationType.ADD, review.getReviewId());
        log.info("Создан отзыв с ID:{}", review.getReviewId());
        return review;
    }

    @Override
    public Review update(@RequestBody Review newReview) {
        reviewStorage.getReviewById(newReview.getReviewId());
        newReview = reviewStorage.update(newReview);
        eventStorage.createEvent(newReview.getUserId(), EventType.REVIEW, OperationType.UPDATE, newReview.getReviewId());
        log.info("Обновлен отзыв с ID:{}", newReview.getReviewId());
        return newReview;
    }

    @Override
    public void delete(@PathVariable int id) {
        Review review = reviewStorage.getReviewById(id);
        eventStorage.createEvent(review.getUserId(), EventType.REVIEW, OperationType.REMOVE, id);
        reviewStorage.deleteById(id);
        log.info("Удален отзыв с ID:{}", id);
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId, int count) {
        filmStorage.getFilmById(filmId);
        log.info("Передан список отзывов к фильму с ID:{}", filmId);
        return reviewStorage.getReviewByFilmId(filmId, count);
    }

    @Override
    public List<Review> getReviewLimit(int count) {
        log.info("Передан список отзывов, ограниченный количеством {}", count);
        return reviewStorage.getReviewLimit(count);
    }

    @Override
    public Review getReviewById(int id) {
        log.info("Передан отзыв с id {}", id);
        return reviewStorage.getReviewById(id);
    }

    @Override
    public void userLikesReview(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        log.info("Обработаны лайки пользователя с id = {}", userId);
        reviewStorage.userLikesReview(id, userId);
    }

    @Override
    public void userDislikesReview(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        log.info("Обработаны дизлайки пользователя с id = {}", userId);
        reviewStorage.userDislikesReview(id, userId);
    }

    @Override
    public void deleteUsersLike(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.deleteUsersLike(id, userId);
        log.info("Удален лайк пользователя с id = {} в ревью с id = {}", userId, id);
        eventStorage.createEvent(userId, EventType.LIKE, OperationType.REMOVE, id);
    }

    @Override
    public void deleteUsersDislike(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.deleteUsersDislike(id, userId);
        log.info("Удален дизлайк пользователя с id = {} в ревью с id = {}", userId, id);
        eventStorage.createEvent(userId, EventType.LIKE, OperationType.ADD, id);
    }

    @Override
    public List<Review> getAllReviews() {
        log.info("Передан список всех отзывов");
        return reviewStorage.getAllReviews();
    }
}
