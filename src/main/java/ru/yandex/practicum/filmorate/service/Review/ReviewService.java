package ru.yandex.practicum.filmorate.service.Review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewService {

    Review create(Review review);

    Review update(Review newReview);

    void delete(int id);

    List<Review> getReviewByFilmId(int filmId, int count);

    List<Review> getReviewLimit(int count);

    Review getReviewById(int id);

    void userLikesReview(int id, int userId);

    void userDislikesReview(int id, int userId);

    void deleteUsersLike(int reviewId, int userId);

    void deleteUsersDislike(int reviewId, int userId);

    List<Review> getAllReviews();
}
