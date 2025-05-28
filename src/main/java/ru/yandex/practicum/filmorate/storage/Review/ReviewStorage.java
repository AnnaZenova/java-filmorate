package ru.yandex.practicum.filmorate.storage.Review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {
    Review create(Review review);

    Review update(Review review);

    void deleteById(int reviewId);

    List<Review> getReviewByFilmId(int filmId, int count);

    List<Review> getReviewLimit(int count);

    Review getReviewById(int id);

    void userLikesReview(int reviewId, int userId);

    void userDislikesReview(int reviewId, int userId);

    void deleteUsersLike(int reviewId, int userId);

    void deleteUsersDislike(int reviewId, int userId);
    List<Review> getAllReviews();
}
