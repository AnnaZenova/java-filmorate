package ru.yandex.practicum.filmorate.service.Review;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.model.enums.EventEnum;
import ru.yandex.practicum.filmorate.model.enums.OperationEnum;
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
        Review resReview = reviewStorage.create(review);
        eventStorage.addEvent(review.getUserId(), EventEnum.REVIEW, OperationEnum.ADD, review.getFilmId());
        return resReview;
    }

    @Override
    public Review update(@RequestBody Review newReview) {
        Review resReview = reviewStorage.update(newReview);
        eventStorage.addEvent(newReview.getUserId(), EventEnum.REVIEW, OperationEnum.UPDATE, newReview.getFilmId());
        return resReview;
    }

    @Override
    public void delete(@PathVariable int id) {
        Review review = reviewStorage.getReviewById(id);
        reviewStorage.deleteById(id);
        eventStorage.addEvent(review.getUserId(), EventEnum.REVIEW, OperationEnum.REMOVE, review.getFilmId());
    }

    @Override
    public List<Review> getReviewByFilmId(int filmId, int count) {
        filmStorage.getFilmById(filmId);
        return reviewStorage.getReviewByFilmId(filmId, count);
    }

    @Override
    public List<Review> getReviewLimit(int count) {
        return reviewStorage.getReviewLimit(count);
    }

    @Override
    public Review getReviewById(int id) {
        return reviewStorage.getReviewById(id);
    }

    @Override
    public void userLikesReview(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.userLikesReview(id, userId);
    }

    @Override
    public void userDislikesReview(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.userDislikesReview(id, userId);
    }

    @Override
    public void deleteUsersLike(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.deleteUsersLike(id, userId);
    }

    @Override
    public void deleteUsersDislike(int id, int userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Пользователь не найден с ID = " + userId);
        }
        reviewStorage.deleteUsersDislike(id, userId);
    }
}
