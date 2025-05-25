package ru.yandex.practicum.filmorate.storage.Review;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.WrongDataException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.*;
import java.util.List;
import java.util.Objects;

@Slf4j
@Repository("ReviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {
    private final JdbcTemplate jdbcTemplate;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    @Transactional
    public Review create(Review review) {
        validateReview(review);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO reviews (review_content, isPositive, user_id, film_id, useful) " +
                "VALUES (?, ?, ?, ?, ?)";

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, review.getContent());
            ps.setBoolean(2, review.getIsPositive());
            ps.setInt(3, review.getUserId());
            ps.setInt(4, review.getFilmId());
            ps.setInt(5, review.getUseful());
            return ps;
        }, keyHolder);
        int reviewId = Objects.requireNonNull(keyHolder.getKey()).intValue();
        review.setReviewId(reviewId);
        log.info("Добавлен новый отзыв с ID={}", review.getReviewId());
        return getReviewById(reviewId);
    }

    @Override
    @Transactional
    public Review update(Review newReview) {
        if (!isReviewExists(newReview.getReviewId())) {
            throw new NotFoundException("Отзыв с ID=" + newReview.getReviewId() + " не найден");
        }
        validateReview(newReview);
        String sql = "UPDATE reviews SET review_content = ?, isPositive = ?, useful = ? WHERE review_id = ?";

        jdbcTemplate.update(sql,
                newReview.getContent(),
                newReview.getIsPositive(),
                newReview.getUseful(),
                newReview.getReviewId());

        log.info("Обновлен отзыв с ID={}", newReview.getReviewId());
        return getReviewById(newReview.getReviewId());
    }

    @Override
    @Transactional
    public void deleteById(int id) {
        if (!isReviewExists(id)) {
            throw new NotFoundException("Отзыв с ID=" + id + " не найден");
        }
        jdbcTemplate.update("DELETE FROM reviews WHERE review_id = ?", id);
        log.info("Удален отзыв с ID={}", id);
    }

    @Override
    @Transactional
    public List<Review> getReviewByFilmId(int film_id, int count) {
        String sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, film_id, count);
    }

    @Override
    @Transactional
    public List<Review> getReviewLimit(int count) {
        String sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, count);
    }

    @Override
    @Transactional
    public Review getReviewById(int id) {
        String sql = "SELECT * FROM reviews WHERE review_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, this::mapRowToReview, id);
        } catch (EmptyResultDataAccessException e) {
            log.info("Отзыв не найден с ID={}", id);
            throw new NotFoundException("Отзыв с ID=" + id + " не найден");
        }
    }

    @Override
    @Transactional
    public void userLikesReview(int reviewId, int userId) {
        if (hasUserLikes(reviewId, userId)) {
            return;
        }

        if (hasUserDislikes(reviewId, userId)) {
            jdbcTemplate.update("DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?", reviewId, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
        }

        jdbcTemplate.update("INSERT INTO review_likes (review_id, user_id) VALUES (?, ?)", reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
    }

    private boolean hasUserLikes(int reviewId, int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM review_likes WHERE user_id = ? AND review_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, reviewId));
    }

    @Override
    @Transactional
    public void userDislikesReview(int reviewId, int userId) {
        if (hasUserDislikes(reviewId, userId)) {
            return;
        }

        if (hasUserLikes(reviewId, userId)) {
            jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);
            jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
        }

        jdbcTemplate.update("INSERT INTO review_dislikes (review_id, user_id) VALUES (?, ?)", reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
    }

    private boolean hasUserDislikes(int reviewId, int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM review_dislikes WHERE user_id = ? AND review_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId, reviewId));
    }

    @Override
    @Transactional
    public void deleteUsersLike(int reviewId, int userId) {
        if (!hasUserLikes(reviewId, userId)) {
            return;
        }

        jdbcTemplate.update("DELETE FROM review_likes WHERE review_id = ? AND user_id = ?", reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful - 1 WHERE review_id = ?", reviewId);
    }

    @Override
    @Transactional
    public void deleteUsersDislike(int reviewId, int userId) {
        if (!hasUserDislikes(reviewId, userId)) {
            return;
        }

        jdbcTemplate.update("DELETE FROM review_dislikes WHERE review_id = ? AND user_id = ?", reviewId, userId);
        jdbcTemplate.update("UPDATE reviews SET useful = useful + 1 WHERE review_id = ?", reviewId);
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getInt("review_id"),
                rs.getString("review_content"),
                rs.getBoolean("isPositive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful")
        );
    }

    private void validateReview(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new WrongDataException("Текст отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new WrongDataException("Оценка отзыва не может быть пустым");
        }
        if (review.getUserId() == null) {
            throw new WrongDataException("ID пользователя не может быть пустым!");
        }
        if (review.getFilmId() == null) {
            throw new WrongDataException("ID фильма не может быть пустым!");
        }
        if (!userExists(review.getUserId())) {
            throw new NotFoundException("Пользователь с ID=" + review.getUserId() + " не найден");
        }

        if (!filmExists(review.getFilmId())) {
            throw new NotFoundException("Фильм с ID=" + review.getFilmId() + " не найден");
        }
    }

    private boolean userExists(int userId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM users WHERE user_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, userId));
    }

    private boolean filmExists(int filmId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM films WHERE film_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, filmId));
    }

    private boolean isReviewExists(int reviewId) {
        String sql = "SELECT EXISTS(SELECT 1 FROM reviews WHERE review_id = ?)";
        return Boolean.TRUE.equals(jdbcTemplate.queryForObject(sql, Boolean.class, reviewId));
    }
}
